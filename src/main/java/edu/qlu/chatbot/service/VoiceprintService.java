package edu.qlu.chatbot.service;

import edu.qlu.chatbot.config.VoiceprintConfig;
import edu.qlu.chatbot.mapper.UserMapper;
import edu.qlu.chatbot.mapper.VoiceprintMapper;
import edu.qlu.chatbot.mapper.VoiceprintIdentificationLogMapper;
import edu.qlu.chatbot.model.*;
import edu.qlu.chatbot.service.AudioProcessingService.AudioProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 声纹服务类
 * 提供声纹注册、识别等核心业务逻辑
 * 
 * @author QLU AI Team
 * @since 1.0.0
 */
@Service
public class VoiceprintService {
    
    private static final Logger logger = LoggerFactory.getLogger(VoiceprintService.class);
    
    private final VoiceprintConfig config;
    private final IFlytekVoiceprintClient iflytekClient;
    private final AudioProcessingService audioProcessingService;
    private final UserMapper userMapper;
    private final VoiceprintMapper voiceprintMapper;
    private final VoiceprintIdentificationLogMapper logMapper;
    
    public VoiceprintService(VoiceprintConfig config,
                           IFlytekVoiceprintClient iflytekClient,
                           AudioProcessingService audioProcessingService,
                           UserMapper userMapper,
                           VoiceprintMapper voiceprintMapper,
                           VoiceprintIdentificationLogMapper logMapper) {
        this.config = config;
        this.iflytekClient = iflytekClient;
        this.audioProcessingService = audioProcessingService;
        this.userMapper = userMapper;
        this.voiceprintMapper = voiceprintMapper;
        this.logMapper = logMapper;
    }
    
    /**
     * 声纹注册
     * 
     * @param userId 用户ID
     * @param audioFile 音频文件
     * @param featureInfo 特征信息（可选）
     * @return 注册响应
     */
    @Transactional
    public VoiceprintEnrollResponse enrollVoiceprint(Long userId, MultipartFile audioFile, String featureInfo) {
        try {
            logger.info("开始声纹注册: userId={}, fileName={}", userId, audioFile.getOriginalFilename());
            
            // 验证用户是否存在
            User user = userMapper.findById(userId);
            if (user == null) {
                return VoiceprintEnrollResponse.error("用户不存在");
            }
            
            if (!user.getIsActive()) {
                return VoiceprintEnrollResponse.error("用户已被禁用");
            }
            
            // 检查用户是否已注册声纹
            if (voiceprintMapper.existsByUserId(userId)) {
                logger.warn("用户已注册声纹: userId={}", userId);
                return VoiceprintEnrollResponse.error("该用户已注册声纹，请先删除现有声纹");
            }
            
            // 处理音频文件
            String audioBase64;
            try {
                audioBase64 = audioProcessingService.processAudioFile(audioFile);
            } catch (AudioProcessingException e) {
                logger.error("音频处理失败: userId={}", userId, e);
                return VoiceprintEnrollResponse.error("音频处理失败: " + e.getMessage());
            }
            
            // 生成唯一的特征ID
            String featureId = generateFeatureId(userId);
            
            // 调用讯飞API注册声纹
            Map<String, Object> apiResult;
            try {
                apiResult = iflytekClient.addAudioFeature(
                    config.getGroupId(), 
                    featureId, 
                    audioBase64, 
                    featureInfo
                );
            } catch (IFlytekApiException e) {
                logger.error("讯飞API调用失败: userId={}, featureId={}", userId, featureId, e);
                return VoiceprintEnrollResponse.error("声纹注册失败: " + e.getMessage());
            }
            
            // 验证API响应
            String returnedFeatureId = (String) apiResult.get("featureId");
            if (returnedFeatureId == null || !returnedFeatureId.equals(featureId)) {
                logger.error("API返回的featureId不匹配: expected={}, actual={}", featureId, returnedFeatureId);
                return VoiceprintEnrollResponse.error("声纹注册失败: 特征ID不匹配");
            }
            
            // 保存到数据库
            Voiceprint voiceprint = new Voiceprint();
            voiceprint.setUserId(userId);
            voiceprint.setIflytekGroupId(config.getGroupId());
            voiceprint.setIflytekFeatureId(featureId);
            voiceprint.setFeatureInfo(featureInfo);
            voiceprint.setAudioFileName(audioFile.getOriginalFilename());
            voiceprint.setRegistrationDate(LocalDateTime.now());
            voiceprint.setIdentificationCount(0);
            voiceprint.setIsActive(true);
            voiceprint.setCreatedAt(LocalDateTime.now());
            voiceprint.setUpdatedAt(LocalDateTime.now());
            
            int insertResult = voiceprintMapper.insert(voiceprint);
            if (insertResult <= 0) {
                logger.error("数据库保存失败: userId={}, featureId={}", userId, featureId);
                
                // 尝试删除已注册的声纹特征
                try {
                    iflytekClient.deleteAudioFeature(config.getGroupId(), featureId);
                } catch (IFlytekApiException deleteException) {
                    logger.error("回滚删除讯飞特征失败: featureId={}", featureId, deleteException);
                }
                
                return VoiceprintEnrollResponse.error("数据库保存失败");
            }
            
            logger.info("声纹注册成功: userId={}, featureId={}, voiceprintId={}", 
                       userId, featureId, voiceprint.getId());
            
            return VoiceprintEnrollResponse.success(
                featureId, 
                userId, 
                user.getUsername(), 
                audioFile.getOriginalFilename()
            );
            
        } catch (Exception e) {
            logger.error("声纹注册异常: userId={}", userId, e);
            return VoiceprintEnrollResponse.error("系统异常: " + e.getMessage());
        }
    }
    
    /**
     * 声纹识别
     * 
     * @param audioFile 音频文件
     * @param request HTTP请求（用于获取客户端信息）
     * @return 识别响应
     */
    @Transactional
    public VoiceprintIdentificationResponse identifyVoiceprint(MultipartFile audioFile, 
                                                             HttpServletRequest request) {
        String requestId = generateRequestId();
        long startTime = System.currentTimeMillis();
        
        try {
            logger.info("开始声纹识别: requestId={}, fileName={}", requestId, audioFile.getOriginalFilename());
            
            // 处理音频文件
            String audioBase64;
            try {
                audioBase64 = audioProcessingService.processAudioFile(audioFile);
            } catch (AudioProcessingException e) {
                logger.error("音频处理失败: requestId={}", requestId, e);
                return VoiceprintIdentificationResponse.error("音频处理失败: " + e.getMessage());
            }
            
            // 调用讯飞API进行声纹识别
            Map<String, Object> apiResult;
            try {
                apiResult = iflytekClient.searchByAudioFeature(
                    config.getGroupId(), 
                    audioBase64, 
                    5  // 返回前5个最匹配的结果
                );
            } catch (IFlytekApiException e) {
                logger.error("讯飞API调用失败: requestId={}", requestId, e);
                
                // 记录识别日志（失败）
                logIdentificationAttempt(requestId, null, null, BigDecimal.ZERO, 
                                       audioFile.getOriginalFilename(), e.getSid(), 
                                       e.getCode(), e.getMessage(), 
                                       (int)(System.currentTimeMillis() - startTime),
                                       request);
                
                return VoiceprintIdentificationResponse.error("声纹识别失败: " + e.getMessage());
            }
            
            // 解析识别结果
            List<Map<String, Object>> scoreList = (List<Map<String, Object>>) apiResult.get("scoreList");
            List<VoiceprintIdentificationResponse.IdentificationResult> results = new ArrayList<>();
            
            if (scoreList != null && !scoreList.isEmpty()) {
                for (Map<String, Object> scoreItem : scoreList) {
                    String featureId = (String) scoreItem.get("featureId");
                    Object scoreObj = scoreItem.get("score");
                    String featureInfo = (String) scoreItem.get("featureInfo");
                    
                    if (featureId != null && scoreObj != null) {
                        BigDecimal score = BigDecimal.valueOf(((Number) scoreObj).doubleValue());
                        
                        // 从数据库查询对应的用户信息
                        Voiceprint voiceprint = voiceprintMapper.findByFeatureIdWithUser(featureId);
                        if (voiceprint != null && voiceprint.getUser() != null) {
                            User user = voiceprint.getUser();
                            
                            VoiceprintIdentificationResponse.IdentificationResult result = 
                                new VoiceprintIdentificationResponse.IdentificationResult(
                                    user.getId(),
                                    user.getUsername(),
                                    user.getFullName(),
                                    featureId,
                                    score,
                                    featureInfo
                                );
                            results.add(result);
                            
                            // 记录识别日志（成功）
                            logIdentificationAttempt(requestId, user.getId(), featureId, score,
                                                   audioFile.getOriginalFilename(), null, 0, null,
                                                   (int)(System.currentTimeMillis() - startTime),
                                                   request);
                            
                            // 更新声纹识别统计
                            voiceprintMapper.updateIdentificationStats(featureId, LocalDateTime.now());
                        }
                    }
                }
                
                // 按置信度得分降序排序
                results.sort((a, b) -> b.getConfidenceScore().compareTo(a.getConfidenceScore()));
            }
            
            int processingDuration = (int)(System.currentTimeMillis() - startTime);
            
            logger.info("声纹识别完成: requestId={}, 匹配数量={}, 处理时间={}ms", 
                       requestId, results.size(), processingDuration);
            
            return VoiceprintIdentificationResponse.success(requestId, results, processingDuration);
            
        } catch (Exception e) {
            logger.error("声纹识别异常: requestId={}", requestId, e);
            
            // 记录识别日志（异常）
            logIdentificationAttempt(requestId, null, null, BigDecimal.ZERO,
                                   audioFile.getOriginalFilename(), null, -1, e.getMessage(),
                                   (int)(System.currentTimeMillis() - startTime),
                                   request);
            
            return VoiceprintIdentificationResponse.error("系统异常: " + e.getMessage());
        }
    }
    
    /**
     * 删除用户声纹
     * 
     * @param userId 用户ID
     * @return 删除是否成功
     */
    @Transactional
    public boolean deleteUserVoiceprint(Long userId) {
        try {
            logger.info("开始删除用户声纹: userId={}", userId);
            
            // 查询用户的声纹记录
            List<Voiceprint> voiceprints = voiceprintMapper.findActiveByUserId(userId);
            if (voiceprints.isEmpty()) {
                logger.warn("用户没有声纹记录: userId={}", userId);
                return false;
            }
            
            boolean allDeleted = true;
            for (Voiceprint voiceprint : voiceprints) {
                try {
                    // 删除讯飞API中的特征
                    iflytekClient.deleteAudioFeature(config.getGroupId(), voiceprint.getIflytekFeatureId());
                    
                    // 软删除数据库记录
                    voiceprintMapper.softDeleteByFeatureId(voiceprint.getIflytekFeatureId());
                    
                    logger.info("声纹删除成功: userId={}, featureId={}", userId, voiceprint.getIflytekFeatureId());
                } catch (Exception e) {
                    logger.error("声纹删除失败: userId={}, featureId={}", 
                               userId, voiceprint.getIflytekFeatureId(), e);
                    allDeleted = false;
                }
            }
            
            return allDeleted;
            
        } catch (Exception e) {
            logger.error("删除用户声纹异常: userId={}", userId, e);
            return false;
        }
    }
    
    /**
     * 获取用户声纹信息
     * 
     * @param userId 用户ID
     * @return 声纹信息列表
     */
    public List<Voiceprint> getUserVoiceprints(Long userId) {
        return voiceprintMapper.findActiveByUserId(userId);
    }
    
    /**
     * 获取识别日志
     * 
     * @param userId 用户ID（可选）
     * @param limit 限制数量
     * @return 识别日志列表
     */
    public List<VoiceprintIdentificationLog> getIdentificationLogs(Long userId, int limit) {
        if (userId != null) {
            return logMapper.findByUserId(userId);
        } else {
            return logMapper.findRecent(limit);
        }
    }
    
    /**
     * 生成特征ID
     */
    private String generateFeatureId(Long userId) {
        return String.format("user_%d_%s", userId, UUID.randomUUID().toString().replace("-", ""));
    }
    
    /**
     * 生成请求ID
     */
    private String generateRequestId() {
        return "req_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
    
    /**
     * 记录识别日志
     */
    private void logIdentificationAttempt(String requestId, Long userId, String featureId, 
                                        BigDecimal score, String audioFileName, String sid, 
                                        Integer responseCode, String responseMessage, 
                                        int duration, HttpServletRequest request) {
        try {
            VoiceprintIdentificationLog log = new VoiceprintIdentificationLog();
            log.setRequestId(requestId);
            log.setIdentifiedUserId(userId);
            log.setIflytekFeatureId(featureId);
            log.setConfidenceScore(score);
            log.setAudioFileName(audioFileName);
            log.setIdentificationTime(LocalDateTime.now());
            log.setApiResponseSid(sid);
            log.setApiResponseCode(responseCode);
            log.setApiResponseMessage(responseMessage);
            log.setProcessingDurationMs(duration);
            log.setClientIp(getClientIp(request));
            log.setUserAgent(request.getHeader("User-Agent"));
            log.setCreatedAt(LocalDateTime.now());
            
            logMapper.insert(log);
        } catch (Exception e) {
            logger.error("记录识别日志失败: requestId={}", requestId, e);
        }
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * 获取声纹统计信息
     */
    public Map<String, Object> getVoiceprintStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 总注册用户数
            long totalUsers = voiceprintMapper.countWithCondition(null, config.getGroupId(), true);
            stats.put("totalRegisteredUsers", totalUsers);
            
            // 今日识别次数
            LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime todayEnd = todayStart.plusDays(1);
            long todayIdentifications = logMapper.countByTimeRange(todayStart, todayEnd);
            stats.put("todayIdentifications", todayIdentifications);
            
            // 总识别次数
            long totalIdentifications = logMapper.count();
            stats.put("totalIdentifications", totalIdentifications);
            
            // 最近7天识别统计
            List<VoiceprintIdentificationLog> recentLogs = logMapper.getStatistics(7);
            stats.put("recentLogs", recentLogs);
            
        } catch (Exception e) {
            logger.error("获取声纹统计信息失败", e);
        }
        
        return stats;
    }
}
