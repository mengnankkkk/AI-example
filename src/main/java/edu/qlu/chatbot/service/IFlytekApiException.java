package edu.qlu.chatbot.service;

/**
 * 讯飞API调用异常类
 * 
 * @author QLU AI Team
 * @since 1.0.0
 */
public class IFlytekApiException extends Exception {
    
    /**
     * 错误代码
     */
    private Integer code;
    
    /**
     * 错误消息
     */
    private String message;
    
    /**
     * 会话ID
     */
    private String sid;
    
    /**
     * 构造函数
     */
    public IFlytekApiException(String message) {
        super(message);
        this.message = message;
    }
    
    /**
     * 构造函数
     */
    public IFlytekApiException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
    
    /**
     * 构造函数
     */
    public IFlytekApiException(String message, String sid, Integer code) {
        super(message);
        this.message = message;
        this.sid = sid;
        this.code = code;
    }
    
    /**
     * 构造函数
     */
    public IFlytekApiException(String message, Integer code, String apiMessage, String sid) {
        super(message);
        this.message = message;
        this.code = code;
        this.sid = sid;
    }
    
    /**
     * 构造函数
     */
    public IFlytekApiException(String message, Integer code, String apiMessage, String sid, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.code = code;
        this.sid = sid;
    }
    
    // Getter方法
    public Integer getCode() {
        return code;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
    
    public String getSid() {
        return sid;
    }
    
    /**
     * 获取详细错误信息
     */
    public String getDetailedMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("IFlytekApiException: ").append(message);
        if (code != null) {
            sb.append(" (code: ").append(code).append(")");
        }
        if (sid != null) {
            sb.append(" (sid: ").append(sid).append(")");
        }
        return sb.toString();
    }
    
    /**
     * 判断是否为认证错误
     */
    public boolean isAuthenticationError() {
        return code != null && code == 10111;
    }
    
    /**
     * 判断是否为参数错误
     */
    public boolean isParameterError() {
        return code != null && (code >= 10100 && code < 10200);
    }
    
    /**
     * 判断是否为系统错误
     */
    public boolean isSystemError() {
        return code != null && (code >= 10200 && code < 10300);
    }
    
    @Override
    public String toString() {
        return "IFlytekApiException{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", sid='" + sid + '\'' +
                '}';
    }
}
