package edu.qlu.chatbot.model;

import java.time.LocalDateTime;

/**
 * 聊天响应DTO
 * 
 * 后端返回聊天响应的数据传输对象
 * 
 * @author AI Assistant
 * @version 1.0.0
 */
public class ChatResponse {

    /**
     * AI助手的回复内容
     */
    private String message;

    /**
     * 会话ID
     */
    private String conversationId;

    /**
     * 响应时间戳
     */
    private LocalDateTime timestamp;

    /**
     * 响应状态
     */
    private ResponseStatus status;

    /**
     * 错误信息（如果有）
     */
    private String error;

    /**
     * 是否是流式响应的一部分
     */
    private boolean isPartial = false;

    // 构造函数
    public ChatResponse() {
        this.timestamp = LocalDateTime.now();
        this.status = ResponseStatus.SUCCESS;
    }

    public ChatResponse(String message, String conversationId) {
        this();
        this.message = message;
        this.conversationId = conversationId;
    }

    // 静态工厂方法
    public static ChatResponse success(String message, String conversationId) {
        return new ChatResponse(message, conversationId);
    }

    public static ChatResponse error(String error, String conversationId) {
        ChatResponse response = new ChatResponse();
        response.setError(error);
        response.setConversationId(conversationId);
        response.setStatus(ResponseStatus.ERROR);
        return response;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isPartial() {
        return isPartial;
    }

    public void setPartial(boolean partial) {
        isPartial = partial;
    }

    @Override
    public String toString() {
        return "ChatResponse{" +
                "message='" + message + '\'' +
                ", conversationId='" + conversationId + '\'' +
                ", timestamp=" + timestamp +
                ", status=" + status +
                ", error='" + error + '\'' +
                ", isPartial=" + isPartial +
                '}';
    }

    /**
     * 响应状态枚举
     */
    public enum ResponseStatus {
        SUCCESS,    // 成功
        ERROR,      // 错误
        PROCESSING  // 处理中
    }
}
