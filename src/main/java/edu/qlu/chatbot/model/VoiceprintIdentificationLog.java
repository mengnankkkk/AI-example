package edu.qlu.chatbot.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 声纹识别日志实体类
 * 对应数据库中的voiceprint_identification_logs表，记录每次识别操作的详细信息
 * 
 * @author QLU AI Team
 * @since 1.0.0
 */
public class VoiceprintIdentificationLog {
    
    private Long id;
    private String requestId;
    private Long identifiedUserId;
    private String iflytekFeatureId;
    private BigDecimal confidenceScore;
    private String audioFileName;
    private LocalDateTime identificationTime;
    private String apiResponseSid;
    private Integer apiResponseCode;
    private String apiResponseMessage;
    private Integer processingDurationMs;
    private String clientIp;
    private String userAgent;
    private LocalDateTime createdAt;
    
    // 关联的用户对象（用于查询时的关联数据）
    private User identifiedUser;
    
    // 默认构造函数
    public VoiceprintIdentificationLog() {}
    
    // 全参构造函数
    public VoiceprintIdentificationLog(Long id, String requestId, Long identifiedUserId,
                                     String iflytekFeatureId, BigDecimal confidenceScore,
                                     String audioFileName, LocalDateTime identificationTime,
                                     String apiResponseSid, Integer apiResponseCode,
                                     String apiResponseMessage, Integer processingDurationMs,
                                     String clientIp, String userAgent, LocalDateTime createdAt) {
        this.id = id;
        this.requestId = requestId;
        this.identifiedUserId = identifiedUserId;
        this.iflytekFeatureId = iflytekFeatureId;
        this.confidenceScore = confidenceScore;
        this.audioFileName = audioFileName;
        this.identificationTime = identificationTime;
        this.apiResponseSid = apiResponseSid;
        this.apiResponseCode = apiResponseCode;
        this.apiResponseMessage = apiResponseMessage;
        this.processingDurationMs = processingDurationMs;
        this.clientIp = clientIp;
        this.userAgent = userAgent;
        this.createdAt = createdAt;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    public Long getIdentifiedUserId() {
        return identifiedUserId;
    }
    
    public void setIdentifiedUserId(Long identifiedUserId) {
        this.identifiedUserId = identifiedUserId;
    }
    
    public String getIflytekFeatureId() {
        return iflytekFeatureId;
    }
    
    public void setIflytekFeatureId(String iflytekFeatureId) {
        this.iflytekFeatureId = iflytekFeatureId;
    }
    
    public BigDecimal getConfidenceScore() {
        return confidenceScore;
    }
    
    public void setConfidenceScore(BigDecimal confidenceScore) {
        this.confidenceScore = confidenceScore;
    }
    
    public String getAudioFileName() {
        return audioFileName;
    }
    
    public void setAudioFileName(String audioFileName) {
        this.audioFileName = audioFileName;
    }
    
    public LocalDateTime getIdentificationTime() {
        return identificationTime;
    }
    
    public void setIdentificationTime(LocalDateTime identificationTime) {
        this.identificationTime = identificationTime;
    }
    
    public String getApiResponseSid() {
        return apiResponseSid;
    }
    
    public void setApiResponseSid(String apiResponseSid) {
        this.apiResponseSid = apiResponseSid;
    }
    
    public Integer getApiResponseCode() {
        return apiResponseCode;
    }
    
    public void setApiResponseCode(Integer apiResponseCode) {
        this.apiResponseCode = apiResponseCode;
    }
    
    public String getApiResponseMessage() {
        return apiResponseMessage;
    }
    
    public void setApiResponseMessage(String apiResponseMessage) {
        this.apiResponseMessage = apiResponseMessage;
    }
    
    public Integer getProcessingDurationMs() {
        return processingDurationMs;
    }
    
    public void setProcessingDurationMs(Integer processingDurationMs) {
        this.processingDurationMs = processingDurationMs;
    }
    
    public String getClientIp() {
        return clientIp;
    }
    
    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public User getIdentifiedUser() {
        return identifiedUser;
    }
    
    public void setIdentifiedUser(User identifiedUser) {
        this.identifiedUser = identifiedUser;
    }
    
    @Override
    public String toString() {
        return "VoiceprintIdentificationLog{" +
                "id=" + id +
                ", requestId='" + requestId + '\'' +
                ", identifiedUserId=" + identifiedUserId +
                ", iflytekFeatureId='" + iflytekFeatureId + '\'' +
                ", confidenceScore=" + confidenceScore +
                ", audioFileName='" + audioFileName + '\'' +
                ", identificationTime=" + identificationTime +
                ", apiResponseSid='" + apiResponseSid + '\'' +
                ", apiResponseCode=" + apiResponseCode +
                ", apiResponseMessage='" + apiResponseMessage + '\'' +
                ", processingDurationMs=" + processingDurationMs +
                ", clientIp='" + clientIp + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
