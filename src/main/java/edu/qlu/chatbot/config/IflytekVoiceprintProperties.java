package edu.qlu.chatbot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 讯飞声纹识别配置属性类
 * 从application.properties中读取讯飞相关配置
 * 
 * @author QLU AI Team
 * @since 1.0.0
 */
@Configuration
@ConfigurationProperties(prefix = "iflytek.voiceprint")
public class IflytekVoiceprintProperties {
    
    /**
     * 讯飞应用ID
     */
    private String appId;
    
    /**
     * 讯飞API密钥
     */
    private String apiKey;
    
    /**
     * 讯飞API秘密
     */
    private String apiSecret;
    
    /**
     * 默认声纹组ID
     */
    private String groupId;
    
    /**
     * API配置
     */
    private Api api = new Api();
    
    /**
     * 音频处理配置
     */
    private Audio audio = new Audio();
    
    // Getter和Setter方法
    public String getAppId() {
        return appId;
    }
    
    public void setAppId(String appId) {
        this.appId = appId;
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public String getApiSecret() {
        return apiSecret;
    }
    
    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }
    
    public String getGroupId() {
        return groupId;
    }
    
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    
    public Api getApi() {
        return api;
    }
    
    public void setApi(Api api) {
        this.api = api;
    }
    
    public Audio getAudio() {
        return audio;
    }
    
    public void setAudio(Audio audio) {
        this.audio = audio;
    }
    
    /**
     * API相关配置
     */
    public static class Api {
        private String host = "api.xf-yun.com";
        private String endpoint = "/v1/private/s782b4996";
        private int connectTimeout = 30000;
        private int readTimeout = 60000;
        
        // Getter和Setter方法
        public String getHost() {
            return host;
        }
        
        public void setHost(String host) {
            this.host = host;
        }
        
        public String getEndpoint() {
            return endpoint;
        }
        
        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }
        
        public int getConnectTimeout() {
            return connectTimeout;
        }
        
        public void setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
        }
        
        public int getReadTimeout() {
            return readTimeout;
        }
        
        public void setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
        }
        
        /**
         * 获取完整的API URL
         */
        public String getFullUrl() {
            return "https://" + host + endpoint;
        }
    }
    
    /**
     * 音频处理相关配置
     */
    public static class Audio {
        private String maxFileSize = "10MB";
        private String allowedFormats = "mp3,wav,m4a,aac,ogg";
        private int targetSampleRate = 16000;
        private int targetChannels = 1;
        private int targetBitDepth = 16;
        
        // Getter和Setter方法
        public String getMaxFileSize() {
            return maxFileSize;
        }
        
        public void setMaxFileSize(String maxFileSize) {
            this.maxFileSize = maxFileSize;
        }
        
        public String getAllowedFormats() {
            return allowedFormats;
        }
        
        public void setAllowedFormats(String allowedFormats) {
            this.allowedFormats = allowedFormats;
        }
        
        public int getTargetSampleRate() {
            return targetSampleRate;
        }
        
        public void setTargetSampleRate(int targetSampleRate) {
            this.targetSampleRate = targetSampleRate;
        }
        
        public int getTargetChannels() {
            return targetChannels;
        }
        
        public void setTargetChannels(int targetChannels) {
            this.targetChannels = targetChannels;
        }
        
        public int getTargetBitDepth() {
            return targetBitDepth;
        }
        
        public void setTargetBitDepth(int targetBitDepth) {
            this.targetBitDepth = targetBitDepth;
        }
        
        /**
         * 获取允许的格式列表
         */
        public String[] getAllowedFormatsArray() {
            return allowedFormats.split(",");
        }
        
        /**
         * 解析最大文件大小为字节数
         */
        public long getMaxFileSizeInBytes() {
            String size = maxFileSize.toLowerCase();
            long multiplier = 1;
            
            if (size.endsWith("kb")) {
                multiplier = 1024;
                size = size.substring(0, size.length() - 2);
            } else if (size.endsWith("mb")) {
                multiplier = 1024 * 1024;
                size = size.substring(0, size.length() - 2);
            } else if (size.endsWith("gb")) {
                multiplier = 1024 * 1024 * 1024;
                size = size.substring(0, size.length() - 2);
            }
            
            try {
                return Long.parseLong(size.trim()) * multiplier;
            } catch (NumberFormatException e) {
                return 10 * 1024 * 1024; // 默认10MB
            }
        }
    }
    
    @Override
    public String toString() {
        return "IflytekVoiceprintProperties{" +
                "appId='" + appId + '\'' +
                ", apiKey='" + (apiKey != null ? "[PROTECTED]" : "null") + '\'' +
                ", apiSecret='" + (apiSecret != null ? "[PROTECTED]" : "null") + '\'' +
                ", groupId='" + groupId + '\'' +
                ", api=" + api +
                ", audio=" + audio +
                '}';
    }
}
