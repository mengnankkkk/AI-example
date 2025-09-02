package edu.qlu.chatbot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 数据采集配置属性类
 * 
 * 从application.properties中读取数据采集相关的配置
 * 
 * @author AI Assistant
 * @version 1.0.0
 */
@Component
@ConfigurationProperties(prefix = "app.data-collection")
public class DataCollectionProperties {

    /**
     * 基础URL列表，用于数据采集
     */
    private List<String> baseUrls = List.of(
            "https://www.qlu.edu.cn",
            "https://teacher.qlu.edu.cn"
    );

    /**
     * 定时任务调度表达式（Cron格式）
     * 默认：每天凌晨2点执行
     */
    private String schedule = "0 0 2 * * ?";

    /**
     * 网络请求超时时间（毫秒）
     */
    private int timeout = 30000;

    /**
     * 失败重试次数
     */
    private int retryCount = 3;

    /**
     * 单次采集的最大页面数量
     */
    private int maxPagesPerSite = 100;

    /**
     * 页面抓取间隔时间（毫秒），避免对目标网站造成压力
     */
    private int crawlDelay = 1000;

    // Getters and Setters
    public List<String> getBaseUrls() {
        return baseUrls;
    }

    public void setBaseUrls(List<String> baseUrls) {
        this.baseUrls = baseUrls;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public int getMaxPagesPerSite() {
        return maxPagesPerSite;
    }

    public void setMaxPagesPerSite(int maxPagesPerSite) {
        this.maxPagesPerSite = maxPagesPerSite;
    }

    public int getCrawlDelay() {
        return crawlDelay;
    }

    public void setCrawlDelay(int crawlDelay) {
        this.crawlDelay = crawlDelay;
    }

    @Override
    public String toString() {
        return "DataCollectionProperties{" +
                "baseUrls=" + baseUrls +
                ", schedule='" + schedule + '\'' +
                ", timeout=" + timeout +
                ", retryCount=" + retryCount +
                ", maxPagesPerSite=" + maxPagesPerSite +
                ", crawlDelay=" + crawlDelay +
                '}';
    }
}
