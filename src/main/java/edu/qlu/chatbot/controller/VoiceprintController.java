package edu.qlu.chatbot.controller;

import edu.qlu.chatbot.model.*;
import edu.qlu.chatbot.service.VoiceprintService;
import edu.qlu.chatbot.service.AudioProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 声纹识别控制器
 * 提供声纹注册、识别等RESTful API接口
 * 
 * @author QLU AI Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/voiceprint")
@CrossOrigin(origins = "*", maxAge = 3600)
public class VoiceprintController {
    
    private static final Logger logger = LoggerFactory.getLogger(VoiceprintController.class);
    
    private final VoiceprintService voiceprintService;
    private final AudioProcessingService audioProcessingService;
    
    public VoiceprintController(VoiceprintService voiceprintService, 
                              AudioProcessingService audioProcessingService) {
        this.voiceprintService = voiceprintService;
        this.audioProcessingService = audioProcessingService;
    }
    
    /**
     * 声纹注册接口
     * 
     * @param userId 用户ID
     * @param file 音频文件
     * @param featureInfo 特征信息（可选）
     * @return 注册结果
     */
    @PostMapping("/enroll")
    public ResponseEntity<VoiceprintEnrollResponse> enrollVoiceprint(
            @RequestParam("userId") Long userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "featureInfo", required = false) String featureInfo) {
        
        try {
            logger.info("收到声纹注册请求: userId={}, fileName={}, fileSize={}", 
                       userId, file.getOriginalFilename(), file.getSize());
            
            // 基础参数验证
            if (userId == null || userId <= 0) {
                VoiceprintEnrollResponse response = VoiceprintEnrollResponse.error("用户ID无效");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (file == null || file.isEmpty()) {
                VoiceprintEnrollResponse response = VoiceprintEnrollResponse.error("音频文件不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 调用服务进行声纹注册
            VoiceprintEnrollResponse response = voiceprintService.enrollVoiceprint(userId, file, featureInfo);
            
            if ("success".equals(response.getStatus())) {
                logger.info("声纹注册成功: userId={}, featureId={}", userId, response.getFeatureId());
                return ResponseEntity.ok(response);
            } else {
                logger.warn("声纹注册失败: userId={}, message={}", userId, response.getMessage());
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            logger.error("声纹注册接口异常: userId={}", userId, e);
            VoiceprintEnrollResponse response = VoiceprintEnrollResponse.error("系统异常: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 声纹识别接口
     * 
     * @param file 音频文件
     * @param request HTTP请求
     * @return 识别结果
     */
    @PostMapping("/identify")
    public ResponseEntity<VoiceprintIdentificationResponse> identifyVoiceprint(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        
        try {
            logger.info("收到声纹识别请求: fileName={}, fileSize={}", 
                       file.getOriginalFilename(), file.getSize());
            
            // 基础参数验证
            if (file == null || file.isEmpty()) {
                VoiceprintIdentificationResponse response = VoiceprintIdentificationResponse.error("音频文件不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 调用服务进行声纹识别
            VoiceprintIdentificationResponse response = voiceprintService.identifyVoiceprint(file, request);
            
            if ("success".equals(response.getStatus())) {
                logger.info("声纹识别完成: requestId={}, 匹配数量={}", 
                           response.getRequestId(), 
                           response.getResults() != null ? response.getResults().size() : 0);
                return ResponseEntity.ok(response);
            } else {
                logger.warn("声纹识别失败: message={}", response.getMessage());
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            logger.error("声纹识别接口异常", e);
            VoiceprintIdentificationResponse response = VoiceprintIdentificationResponse.error("系统异常: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 删除用户声纹接口
     * 
     * @param userId 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUserVoiceprint(@PathVariable Long userId) {
        try {
            logger.info("收到删除声纹请求: userId={}", userId);
            
            if (userId == null || userId <= 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "用户ID无效");
                return ResponseEntity.badRequest().body(response);
            }
            
            boolean deleted = voiceprintService.deleteUserVoiceprint(userId);
            
            Map<String, Object> response = new HashMap<>();
            if (deleted) {
                response.put("status", "success");
                response.put("message", "声纹删除成功");
                logger.info("声纹删除成功: userId={}", userId);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "声纹删除失败或用户没有声纹记录");
                logger.warn("声纹删除失败: userId={}", userId);
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            logger.error("删除声纹接口异常: userId={}", userId, e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "系统异常: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 获取用户声纹信息接口
     * 
     * @param userId 用户ID
     * @return 用户声纹信息
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserVoiceprints(@PathVariable Long userId) {
        try {
            logger.info("收到查询用户声纹请求: userId={}", userId);
            
            if (userId == null || userId <= 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "用户ID无效");
                return ResponseEntity.badRequest().body(response);
            }
            
            List<Voiceprint> voiceprints = voiceprintService.getUserVoiceprints(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("userId", userId);
            response.put("voiceprints", voiceprints);
            response.put("count", voiceprints.size());
            
            logger.info("查询用户声纹完成: userId={}, count={}", userId, voiceprints.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("查询用户声纹接口异常: userId={}", userId, e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "系统异常: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 获取识别日志接口
     * 
     * @param userId 用户ID（可选）
     * @param limit 限制数量
     * @return 识别日志列表
     */
    @GetMapping("/logs")
    public ResponseEntity<Map<String, Object>> getIdentificationLogs(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        
        try {
            logger.info("收到查询识别日志请求: userId={}, limit={}", userId, limit);
            
            if (limit <= 0 || limit > 1000) {
                limit = 50; // 默认限制
            }
            
            List<VoiceprintIdentificationLog> logs = voiceprintService.getIdentificationLogs(userId, limit);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("logs", logs);
            response.put("count", logs.size());
            response.put("userId", userId);
            response.put("limit", limit);
            
            logger.info("查询识别日志完成: userId={}, count={}", userId, logs.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("查询识别日志接口异常: userId={}", userId, e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "系统异常: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 获取音频文件信息接口
     * 
     * @param file 音频文件
     * @return 音频文件信息
     */
    @PostMapping("/audio/info")
    public ResponseEntity<Map<String, Object>> getAudioInfo(@RequestParam("file") MultipartFile file) {
        try {
            logger.info("收到查询音频信息请求: fileName={}", file.getOriginalFilename());
            
            if (file == null || file.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "音频文件不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            AudioProcessingService.AudioFileInfo audioInfo = audioProcessingService.getAudioFileInfo(file);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("audioInfo", audioInfo);
            
            logger.info("查询音频信息完成: fileName={}, duration={}s", 
                       audioInfo.getFileName(), audioInfo.getDuration());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("查询音频信息接口异常", e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "系统异常: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 获取声纹统计信息接口
     * 
     * @return 统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        try {
            logger.info("收到查询声纹统计信息请求");
            
            Map<String, Object> statistics = voiceprintService.getVoiceprintStatistics();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("statistics", statistics);
            
            logger.info("查询声纹统计信息完成");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("查询声纹统计信息接口异常", e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "系统异常: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 健康检查接口
     * 
     * @return 健康状态
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("service", "voiceprint");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}
