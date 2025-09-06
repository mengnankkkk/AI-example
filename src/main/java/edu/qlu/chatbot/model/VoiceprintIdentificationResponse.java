package edu.qlu.chatbot.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 声纹识别响应DTO
 * 
 * @author QLU AI Team
 * @since 1.0.0
 */
public class VoiceprintIdentificationResponse {
    
    private String status;
    private String message;
    private String requestId;
    private List<IdentificationResult> results;
    private LocalDateTime identificationTime;
    private Integer processingDurationMs;
    
    // 默认构造函数
    public VoiceprintIdentificationResponse() {}
    
    // 成功响应构造函数
    public VoiceprintIdentificationResponse(String requestId, List<IdentificationResult> results, 
                                          Integer processingDurationMs) {
        this.status = "success";
        this.message = results.isEmpty() ? "未找到匹配的声纹" : "声纹识别完成";
        this.requestId = requestId;
        this.results = results;
        this.identificationTime = LocalDateTime.now();
        this.processingDurationMs = processingDurationMs;
    }
    
    // 错误响应构造函数
    public VoiceprintIdentificationResponse(String status, String message) {
        this.status = status;
        this.message = message;
        this.identificationTime = LocalDateTime.now();
    }
    
    // 创建成功响应的静态方法
    public static VoiceprintIdentificationResponse success(String requestId, List<IdentificationResult> results, 
                                                          Integer processingDurationMs) {
        return new VoiceprintIdentificationResponse(requestId, results, processingDurationMs);
    }
    
    // 创建错误响应的静态方法
    public static VoiceprintIdentificationResponse error(String message) {
        return new VoiceprintIdentificationResponse("error", message);
    }
    
    // Getter和Setter方法
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    public List<IdentificationResult> getResults() {
        return results;
    }
    
    public void setResults(List<IdentificationResult> results) {
        this.results = results;
    }
    
    public LocalDateTime getIdentificationTime() {
        return identificationTime;
    }
    
    public void setIdentificationTime(LocalDateTime identificationTime) {
        this.identificationTime = identificationTime;
    }
    
    public Integer getProcessingDurationMs() {
        return processingDurationMs;
    }
    
    public void setProcessingDurationMs(Integer processingDurationMs) {
        this.processingDurationMs = processingDurationMs;
    }
    
    /**
     * 识别结果内部类
     */
    public static class IdentificationResult {
        private Long userId;
        private String username;
        private String fullName;
        private String featureId;
        private BigDecimal confidenceScore;
        private String featureInfo;
        
        // 默认构造函数
        public IdentificationResult() {}
        
        // 全参构造函数
        public IdentificationResult(Long userId, String username, String fullName, 
                                  String featureId, BigDecimal confidenceScore, String featureInfo) {
            this.userId = userId;
            this.username = username;
            this.fullName = fullName;
            this.featureId = featureId;
            this.confidenceScore = confidenceScore;
            this.featureInfo = featureInfo;
        }
        
        // Getter和Setter方法
        public Long getUserId() {
            return userId;
        }
        
        public void setUserId(Long userId) {
            this.userId = userId;
        }
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getFullName() {
            return fullName;
        }
        
        public void setFullName(String fullName) {
            this.fullName = fullName;
        }
        
        public String getFeatureId() {
            return featureId;
        }
        
        public void setFeatureId(String featureId) {
            this.featureId = featureId;
        }
        
        public BigDecimal getConfidenceScore() {
            return confidenceScore;
        }
        
        public void setConfidenceScore(BigDecimal confidenceScore) {
            this.confidenceScore = confidenceScore;
        }
        
        public String getFeatureInfo() {
            return featureInfo;
        }
        
        public void setFeatureInfo(String featureInfo) {
            this.featureInfo = featureInfo;
        }
        
        @Override
        public String toString() {
            return "IdentificationResult{" +
                    "userId=" + userId +
                    ", username='" + username + '\'' +
                    ", fullName='" + fullName + '\'' +
                    ", featureId='" + featureId + '\'' +
                    ", confidenceScore=" + confidenceScore +
                    ", featureInfo='" + featureInfo + '\'' +
                    '}';
        }
    }
    
    // 判断是否成功的方法
    public boolean isSuccess() {
        return "success".equals(status);
    }
    
    @Override
    public String toString() {
        return "VoiceprintIdentificationResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", requestId='" + requestId + '\'' +
                ", results=" + results +
                ", identificationTime=" + identificationTime +
                ", processingDurationMs=" + processingDurationMs +
                '}';
    }
}
