package edu.qlu.chatbot.model;

/**
 * 聊天请求DTO
 * 
 * 前端发送聊天消息的数据传输对象
 * 
 * @author AI Assistant
 * @version 1.0.0
 */
public class ChatRequest {

    /**
     * 用户输入的消息
     */
    private String message;

    /**
     * 会话ID，用于标识不同的对话会话
     */
    private String conversationId;

    /**
     * 是否流式响应
     */
    private boolean stream = true;

    // 构造函数
    public ChatRequest() {}

    public ChatRequest(String message, String conversationId) {
        this.message = message;
        this.conversationId = conversationId;
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

    public boolean isStream() {
        return stream;
    }

    public void setStream(boolean stream) {
        this.stream = stream;
    }

    @Override
    public String toString() {
        return "ChatRequest{" +
                "message='" + message + '\'' +
                ", conversationId='" + conversationId + '\'' +
                ", stream=" + stream +
                '}';
    }
}
