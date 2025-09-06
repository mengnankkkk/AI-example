package edu.qlu.chatbot.model;

/**
 * 声纹注册响应DTO
 * 
 * @author QLU AI Team
 * @since 1.0.0
 */
public class VoiceprintEnrollResponse {
    
    private String status;
    private String message;
    private String featureId;
    private Long userId;
    private String username;
    private String audioFileName;
    
    // 默认构造函数
    public VoiceprintEnrollResponse() {}
    
    // 成功响应构造函数
    public VoiceprintEnrollResponse(String featureId, Long userId, String username, String audioFileName) {
        this.status = "success";
        this.message = "声纹注册成功";
        this.featureId = featureId;
        this.userId = userId;
        this.username = username;
        this.audioFileName = audioFileName;
    }
    
    // 错误响应构造函数
    public VoiceprintEnrollResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }
    
    // 创建成功响应的静态方法
    public static VoiceprintEnrollResponse success(String featureId, Long userId, String username, String audioFileName) {
        return new VoiceprintEnrollResponse(featureId, userId, username, audioFileName);
    }
    
    // 创建错误响应的静态方法
    public static VoiceprintEnrollResponse error(String message) {
        return new VoiceprintEnrollResponse("error", message);
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
    
    public String getFeatureId() {
        return featureId;
    }
    
    public void setFeatureId(String featureId) {
        this.featureId = featureId;
    }
    
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
    
    public String getAudioFileName() {
        return audioFileName;
    }
    
    public void setAudioFileName(String audioFileName) {
        this.audioFileName = audioFileName;
    }
    
    // 判断是否成功的方法
    public boolean isSuccess() {
        return "success".equals(status);
    }
    
    @Override
    public String toString() {
        return "VoiceprintEnrollResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", featureId='" + featureId + '\'' +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", audioFileName='" + audioFileName + '\'' +
                '}';
    }
}
