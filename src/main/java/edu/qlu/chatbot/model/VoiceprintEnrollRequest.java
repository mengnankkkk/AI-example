package edu.qlu.chatbot.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 声纹注册请求DTO
 * 
 * @author QLU AI Team
 * @since 1.0.0
 */
public class VoiceprintEnrollRequest {
    
    @NotNull(message = "用户ID不能为空")
    @Positive(message = "用户ID必须为正数")
    private Long userId;
    
    private String featureInfo;
    
    // 默认构造函数
    public VoiceprintEnrollRequest() {}
    
    // 构造函数
    public VoiceprintEnrollRequest(Long userId, String featureInfo) {
        this.userId = userId;
        this.featureInfo = featureInfo;
    }
    
    // Getter和Setter方法
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getFeatureInfo() {
        return featureInfo;
    }
    
    public void setFeatureInfo(String featureInfo) {
        this.featureInfo = featureInfo;
    }
    
    @Override
    public String toString() {
        return "VoiceprintEnrollRequest{" +
                "userId=" + userId +
                ", featureInfo='" + featureInfo + '\'' +
                '}';
    }
}
