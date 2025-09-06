package edu.qlu.chatbot.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 声纹记录实体类
 * 对应数据库中的voiceprints表，存储用户与讯飞声纹特征ID的映射关系
 * 
 * @author QLU AI Team
 * @since 1.0.0
 */
public class Voiceprint {
    
    private Long id;
    private Long userId;
    private String iflytekGroupId;
    private String iflytekFeatureId;
    private String featureInfo;
    private String audioFileName;
    private LocalDateTime registrationDate;
    private LocalDateTime lastIdentifiedAt;
    private Integer identificationCount;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 关联的用户对象（用于查询时的关联数据）
    private User user;
    
    // 默认构造函数
    public Voiceprint() {
        this.identificationCount = 0;
        this.isActive = true;
    }
    
    // 全参构造函数
    public Voiceprint(Long id, Long userId, String iflytekGroupId, String iflytekFeatureId,
                     String featureInfo, String audioFileName, LocalDateTime registrationDate,
                     LocalDateTime lastIdentifiedAt, Integer identificationCount, Boolean isActive,
                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.iflytekGroupId = iflytekGroupId;
        this.iflytekFeatureId = iflytekFeatureId;
        this.featureInfo = featureInfo;
        this.audioFileName = audioFileName;
        this.registrationDate = registrationDate;
        this.lastIdentifiedAt = lastIdentifiedAt;
        this.identificationCount = identificationCount;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getIflytekGroupId() {
        return iflytekGroupId;
    }
    
    public void setIflytekGroupId(String iflytekGroupId) {
        this.iflytekGroupId = iflytekGroupId;
    }
    
    public String getIflytekFeatureId() {
        return iflytekFeatureId;
    }
    
    public void setIflytekFeatureId(String iflytekFeatureId) {
        this.iflytekFeatureId = iflytekFeatureId;
    }
    
    public String getFeatureInfo() {
        return featureInfo;
    }
    
    public void setFeatureInfo(String featureInfo) {
        this.featureInfo = featureInfo;
    }
    
    public String getAudioFileName() {
        return audioFileName;
    }
    
    public void setAudioFileName(String audioFileName) {
        this.audioFileName = audioFileName;
    }
    
    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    public LocalDateTime getLastIdentifiedAt() {
        return lastIdentifiedAt;
    }
    
    public void setLastIdentifiedAt(LocalDateTime lastIdentifiedAt) {
        this.lastIdentifiedAt = lastIdentifiedAt;
    }
    
    public Integer getIdentificationCount() {
        return identificationCount;
    }
    
    public void setIdentificationCount(Integer identificationCount) {
        this.identificationCount = identificationCount;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    /**
     * 增加识别次数计数
     */
    public void incrementIdentificationCount() {
        if (this.identificationCount == null) {
            this.identificationCount = 0;
        }
        this.identificationCount++;
        this.lastIdentifiedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Voiceprint{" +
                "id=" + id +
                ", userId=" + userId +
                ", iflytekGroupId='" + iflytekGroupId + '\'' +
                ", iflytekFeatureId='" + iflytekFeatureId + '\'' +
                ", featureInfo='" + featureInfo + '\'' +
                ", audioFileName='" + audioFileName + '\'' +
                ", registrationDate=" + registrationDate +
                ", lastIdentifiedAt=" + lastIdentifiedAt +
                ", identificationCount=" + identificationCount +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
