package edu.qlu.chatbot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.qlu.chatbot.config.VoiceprintConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

/**
 * 讯飞声纹识别API客户端
 * 封装与讯飞声纹识别WebAPI的所有交互细节
 * 
 * @author QLU AI Team
 * @since 1.0.0
 */
@Component
public class IFlytekVoiceprintClient {
    
    private static final Logger logger = LoggerFactory.getLogger(IFlytekVoiceprintClient.class);
    
    private final VoiceprintConfig config;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    
    // 日期格式化器（RFC1123格式）
    private static final DateTimeFormatter RFC1123_FORMATTER = 
        DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH);
    
    public IFlytekVoiceprintClient(VoiceprintConfig config) {
        this.config = config;
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(java.time.Duration.ofMillis(config.getApi().getConnectTimeout()))
            .build();
    }
    
    /**
     * 添加音频特征（声纹注册）
     * 
     * @param groupId 声纹库组ID
     * @param featureId 特征ID
     * @param audioBase64 Base64编码的音频数据
     * @param featureInfo 特征信息（可选）
     * @return API响应结果
     * @throws IFlytekApiException API调用异常
     */
    public Map<String, Object> addAudioFeature(String groupId, String featureId, 
                                              String audioBase64, String featureInfo) throws IFlytekApiException {
        logger.info("开始添加音频特征: groupId={}, featureId={}", groupId, featureId);
        
        try {
            // 构建请求体
            Map<String, Object> requestBody = buildCreateFeatureRequest(groupId, featureId, audioBase64, featureInfo);
            
            // 发送请求
            Map<String, Object> response = sendRequest("createFeature", requestBody);
            
            // 解析响应
            return parseCreateFeatureResponse(response);
            
        } catch (Exception e) {
            logger.error("添加音频特征失败: groupId={}, featureId={}", groupId, featureId, e);
            throw new IFlytekApiException("添加音频特征失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 通过音频进行1:N声纹检索
     * 
     * @param groupId 声纹库组ID
     * @param audioBase64 Base64编码的音频数据
     * @param topK 返回最匹配结果的数量
     * @return API响应结果
     * @throws IFlytekApiException API调用异常
     */
    public Map<String, Object> searchByAudioFeature(String groupId, String audioBase64, 
                                                   int topK) throws IFlytekApiException {
        logger.info("开始声纹检索: groupId={}, topK={}", groupId, topK);
        
        try {
            // 构建请求体
            Map<String, Object> requestBody = buildSearchFeatureRequest(groupId, audioBase64, topK);
            
            // 发送请求
            Map<String, Object> response = sendRequest("searchFea", requestBody);
            
            // 解析响应
            return parseSearchFeatureResponse(response);
            
        } catch (Exception e) {
            logger.error("声纹检索失败: groupId={}", groupId, e);
            throw new IFlytekApiException("声纹检索失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 删除指定特征
     * 
     * @param groupId 声纹库组ID
     * @param featureId 特征ID
     * @return API响应结果
     * @throws IFlytekApiException API调用异常
     */
    public Map<String, Object> deleteAudioFeature(String groupId, String featureId) throws IFlytekApiException {
        logger.info("开始删除音频特征: groupId={}, featureId={}", groupId, featureId);
        
        try {
            // 构建请求体
            Map<String, Object> requestBody = buildDeleteFeatureRequest(groupId, featureId);
            
            // 发送请求
            Map<String, Object> response = sendRequest("deleteFeature", requestBody);
            
            // 解析响应
            return parseDeleteFeatureResponse(response);
            
        } catch (Exception e) {
            logger.error("删除音频特征失败: groupId={}, featureId={}", groupId, featureId, e);
            throw new IFlytekApiException("删除音频特征失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 创建声纹特征库（辅助方法）
     * 
     * @param groupId 声纹库组ID
     * @param groupName 声纹库名称
     * @param groupInfo 声纹库信息（可选）
     * @return API响应结果
     * @throws IFlytekApiException API调用异常
     */
    public Map<String, Object> createFeatureGroup(String groupId, String groupName, 
                                                 String groupInfo) throws IFlytekApiException {
        logger.info("开始创建声纹特征库: groupId={}, groupName={}", groupId, groupName);
        
        try {
            // 构建请求体
            Map<String, Object> requestBody = buildCreateGroupRequest(groupId, groupName, groupInfo);
            
            // 发送请求
            Map<String, Object> response = sendRequest("createGroup", requestBody);
            
            // 解析响应
            return parseCreateGroupResponse(response);
            
        } catch (Exception e) {
            logger.error("创建声纹特征库失败: groupId={}, groupName={}", groupId, groupName, e);
            throw new IFlytekApiException("创建声纹特征库失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 发送HTTP请求到讯飞API
     */
    private Map<String, Object> sendRequest(String func, Map<String, Object> requestBody) throws Exception {
        String requestJson = objectMapper.writeValueAsString(requestBody);
        logger.debug("发送请求: func={}, body={}", func, requestJson);
        
        // 生成认证头
        Map<String, String> authHeaders = generateAuthHeaders("POST", config.getApi().getEndpoint());
        
        // 构建HTTP请求
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create(config.getApi().getFullUrl()))
            .timeout(java.time.Duration.ofMillis(config.getApi().getReadTimeout()))
            .header("Content-Type", "application/json; charset=utf-8")
            .POST(HttpRequest.BodyPublishers.ofString(requestJson, StandardCharsets.UTF_8));
        
        // 添加认证头
        for (Map.Entry<String, String> header : authHeaders.entrySet()) {
            requestBuilder.header(header.getKey(), header.getValue());
        }
        
        HttpRequest request = requestBuilder.build();
        
        // 发送请求
        long startTime = System.currentTimeMillis();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        long duration = System.currentTimeMillis() - startTime;
        
        logger.debug("收到响应: status={}, duration={}ms", response.statusCode(), duration);
        
        if (response.statusCode() != 200) {
            throw new IFlytekApiException("HTTP请求失败: " + response.statusCode() + " " + response.body());
        }
        
        // 解析响应JSON
        Map<String, Object> responseMap = objectMapper.readValue(response.body(), Map.class);
        
        // 检查API响应码
        Map<String, Object> header = (Map<String, Object>) responseMap.get("header");
        if (header != null) {
            Integer code = (Integer) header.get("code");
            String sid = (String) header.get("sid");
            
            if (code != null && code != 0) {
                String message = (String) header.get("message");
                logger.error("API调用失败: code={}, message={}, sid={}", code, message, sid);
                throw new IFlytekApiException("API调用失败: " + code + " " + message, code, message, sid);
            }
            
            logger.debug("API调用成功: sid={}", sid);
        }
        
        return responseMap;
    }
    
    /**
     * 生成HMAC-SHA256认证头
     */
    private Map<String, String> generateAuthHeaders(String httpMethod, String requestPath) throws Exception {
        // 获取当前GMT时间
        String dateString = ZonedDateTime.now(ZoneOffset.UTC).format(RFC1123_FORMATTER);
        
        // 构建签名原文
        String signatureOrigin = String.format(
            "host: %s\ndate: %s\n%s %s HTTP/1.1",
            config.getApi().getHost(),
            dateString,
            httpMethod.toUpperCase(),
            requestPath
        );
        
        logger.debug("签名原文: {}", signatureOrigin);
        
        // 计算HMAC-SHA256签名
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(
            config.getApiSecret().getBytes(StandardCharsets.UTF_8), 
            "HmacSHA256"
        );
        mac.init(secretKeySpec);
        byte[] signature = mac.doFinal(signatureOrigin.getBytes(StandardCharsets.UTF_8));
        
        // Base64编码签名
        String signatureBase64 = Base64.getEncoder().encodeToString(signature);
        
        // 构建Authorization头
        String authString = String.format(
            "api_key=\"%s\",algorithm=\"hmac-sha256\",headers=\"host date request-line\",signature=\"%s\"",
            config.getApiKey(),
            signatureBase64
        );
        
        // 返回认证头
        Map<String, String> headers = new HashMap<>();
        headers.put("Host", config.getApi().getHost());
        headers.put("Date", dateString);
        headers.put("Authorization", authString);
        
        return headers;
    }
    
    /**
     * 构建创建特征请求体
     */
    private Map<String, Object> buildCreateFeatureRequest(String groupId, String featureId, 
                                                        String audioBase64, String featureInfo) {
        Map<String, Object> header = new HashMap<>();
        header.put("app_id", config.getAppId());
        header.put("status", 3);
        
        Map<String, Object> parameter = new HashMap<>();
        Map<String, Object> s782b4996 = new HashMap<>();
        s782b4996.put("func", "createFeature");
        s782b4996.put("groupId", groupId);
        s782b4996.put("featureId", featureId);
        if (featureInfo != null && !featureInfo.trim().isEmpty()) {
            s782b4996.put("featureInfo", featureInfo);
        }
        parameter.put("s782b4996", s782b4996);
        
        Map<String, Object> payload = new HashMap<>();
        Map<String, Object> resource = new HashMap<>();
        resource.put("audio", audioBase64);
        payload.put("resource", resource);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("header", header);
        requestBody.put("parameter", parameter);
        requestBody.put("payload", payload);
        
        return requestBody;
    }
    
    /**
     * 构建搜索特征请求体
     */
    private Map<String, Object> buildSearchFeatureRequest(String groupId, String audioBase64, int topK) {
        Map<String, Object> header = new HashMap<>();
        header.put("app_id", config.getAppId());
        header.put("status", 3);
        
        Map<String, Object> parameter = new HashMap<>();
        Map<String, Object> s782b4996 = new HashMap<>();
        s782b4996.put("func", "searchFea");
        s782b4996.put("groupId", groupId);
        s782b4996.put("topK", topK);
        parameter.put("s782b4996", s782b4996);
        
        Map<String, Object> payload = new HashMap<>();
        Map<String, Object> resource = new HashMap<>();
        resource.put("audio", audioBase64);
        payload.put("resource", resource);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("header", header);
        requestBody.put("parameter", parameter);
        requestBody.put("payload", payload);
        
        return requestBody;
    }
    
    /**
     * 构建删除特征请求体
     */
    private Map<String, Object> buildDeleteFeatureRequest(String groupId, String featureId) {
        Map<String, Object> header = new HashMap<>();
        header.put("app_id", config.getAppId());
        header.put("status", 3);
        
        Map<String, Object> parameter = new HashMap<>();
        Map<String, Object> s782b4996 = new HashMap<>();
        s782b4996.put("func", "deleteFeature");
        s782b4996.put("groupId", groupId);
        s782b4996.put("featureId", featureId);
        parameter.put("s782b4996", s782b4996);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("header", header);
        requestBody.put("parameter", parameter);
        
        return requestBody;
    }
    
    /**
     * 构建创建组请求体
     */
    private Map<String, Object> buildCreateGroupRequest(String groupId, String groupName, String groupInfo) {
        Map<String, Object> header = new HashMap<>();
        header.put("app_id", config.getAppId());
        header.put("status", 3);
        
        Map<String, Object> parameter = new HashMap<>();
        Map<String, Object> s782b4996 = new HashMap<>();
        s782b4996.put("func", "createGroup");
        s782b4996.put("groupId", groupId);
        s782b4996.put("groupName", groupName);
        if (groupInfo != null && !groupInfo.trim().isEmpty()) {
            s782b4996.put("groupInfo", groupInfo);
        }
        parameter.put("s782b4996", s782b4996);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("header", header);
        requestBody.put("parameter", parameter);
        
        return requestBody;
    }
    
    /**
     * 解析创建特征响应
     */
    private Map<String, Object> parseCreateFeatureResponse(Map<String, Object> response) throws Exception {
        Map<String, Object> payload = (Map<String, Object>) response.get("payload");
        if (payload != null) {
            Map<String, Object> createFeatureRes = (Map<String, Object>) payload.get("createFeatureRes");
            if (createFeatureRes != null) {
                String text = (String) createFeatureRes.get("text");
                if (text != null) {
                    // Base64解码
                    String decodedText = new String(Base64.getDecoder().decode(text), StandardCharsets.UTF_8);
                    // 解析JSON
                    return objectMapper.readValue(decodedText, Map.class);
                }
            }
        }
        throw new IFlytekApiException("无法解析创建特征响应");
    }
    
    /**
     * 解析搜索特征响应
     */
    private Map<String, Object> parseSearchFeatureResponse(Map<String, Object> response) throws Exception {
        Map<String, Object> payload = (Map<String, Object>) response.get("payload");
        if (payload != null) {
            Map<String, Object> searchFeaRes = (Map<String, Object>) payload.get("searchFeaRes");
            if (searchFeaRes != null) {
                String text = (String) searchFeaRes.get("text");
                if (text != null) {
                    // Base64解码
                    String decodedText = new String(Base64.getDecoder().decode(text), StandardCharsets.UTF_8);
                    // 解析JSON
                    return objectMapper.readValue(decodedText, Map.class);
                }
            }
        }
        throw new IFlytekApiException("无法解析搜索特征响应");
    }
    
    /**
     * 解析删除特征响应
     */
    private Map<String, Object> parseDeleteFeatureResponse(Map<String, Object> response) throws Exception {
        Map<String, Object> payload = (Map<String, Object>) response.get("payload");
        if (payload != null) {
            Map<String, Object> deleteFeatureRes = (Map<String, Object>) payload.get("deleteFeatureRes");
            if (deleteFeatureRes != null) {
                String text = (String) deleteFeatureRes.get("text");
                if (text != null) {
                    // Base64解码
                    String decodedText = new String(Base64.getDecoder().decode(text), StandardCharsets.UTF_8);
                    // 解析JSON
                    return objectMapper.readValue(decodedText, Map.class);
                }
            }
        }
        throw new IFlytekApiException("无法解析删除特征响应");
    }
    
    /**
     * 解析创建组响应
     */
    private Map<String, Object> parseCreateGroupResponse(Map<String, Object> response) throws Exception {
        Map<String, Object> payload = (Map<String, Object>) response.get("payload");
        if (payload != null) {
            Map<String, Object> createGroupRes = (Map<String, Object>) payload.get("createGroupRes");
            if (createGroupRes != null) {
                String text = (String) createGroupRes.get("text");
                if (text != null) {
                    // Base64解码
                    String decodedText = new String(Base64.getDecoder().decode(text), StandardCharsets.UTF_8);
                    // 解析JSON
                    return objectMapper.readValue(decodedText, Map.class);
                }
            }
        }
        throw new IFlytekApiException("无法解析创建组响应");
    }
}
