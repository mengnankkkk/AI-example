package edu.qlu.chatbot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 讯飞声纹识别配置类
 * 
 * @author QLU AI Team
 * @since 1.0.0
 */
@Component
@ConfigurationProperties(prefix = "iflytek.voiceprint")
public class VoiceprintConfig {
    
    /**
     * 讯飞应用ID
     */
    private String appId;
    
    /**
     * 讯飞API密钥
     */
    private String apiKey;
    
    /**
     * 讯飞API密钥
     */
    private String apiSecret;
    
    /**
     * 声纹库组ID
     */
    private String groupId;
    
    /**
     * API配置
     */
    private Api api = new Api();
    
    /**
     * 音频配置
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
     * API配置内部类
     */
    public static class Api {
        /**
         * API主机名
         */
        private String host = "api.xf-yun.com";
        
        /**
         * API端点路径
         */
        private String endpoint = "/v1/private/s782b4996";
        
        /**
         * 连接超时时间（毫秒）
         */
        private int connectTimeout = 30000;
        
        /**
         * 读取超时时间（毫秒）
         */
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
     * 音频配置内部类
     */
    public static class Audio {
        /**
         * 最大文件大小
         */
        private String maxFileSize = "10MB";
        
        /**
         * 允许的文件格式
         */
        private String allowedFormats = "mp3,wav,m4a,aac,ogg";
        
        /**
         * 目标采样率
         */
        private int targetSampleRate = 16000;
        
        /**
         * 目标声道数
         */
        private int targetChannels = 1;
        
        /**
         * 目标位深
         */
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
         * 获取允许的文件格式数组
         */
        public String[] getAllowedFormatsArray() {
            return allowedFormats.split(",");
        }
        
        /**
         * 解析最大文件大小为字节数
         */
        public long getMaxFileSizeInBytes() {
            String size = maxFileSize.toUpperCase();
            if (size.endsWith("MB")) {
                return Long.parseLong(size.substring(0, size.length() - 2)) * 1024 * 1024;
            } else if (size.endsWith("KB")) {
                return Long.parseLong(size.substring(0, size.length() - 2)) * 1024;
            } else if (size.endsWith("B")) {
                return Long.parseLong(size.substring(0, size.length() - 1));
            } else {
                // 默认按字节处理
                return Long.parseLong(size);
            }
        }
    }
    
    @Override
    public String toString() {
        return "VoiceprintConfig{" +
                "appId='" + appId + '\'' +
                ", apiKey='" + (apiKey != null ? "***" : null) + '\'' +
                ", apiSecret='" + (apiSecret != null ? "***" : null) + '\'' +
                ", groupId='" + groupId + '\'' +
                ", api=" + api.getFullUrl() +
                ", audio.maxFileSize='" + audio.maxFileSize + '\'' +
                '}';
    }
}
