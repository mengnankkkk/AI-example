package edu.qlu.chatbot.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 知识文档实体类
 * 
 * 用于存储从齐鲁工业大学官网采集到的文档信息
 * 
 * @author AI Assistant
 * @version 1.0.0
 */
public class KnowledgeDocument {

    private Long id;

    /**
     * 文档标题
     */
    private String title;

    /**
     * 文档内容
     */
    private String content;

    /**
     * 原始URL
     */
    private String sourceUrl;

    /**
     * 文档类型（如：新闻、通知、规章制度等）
     */
    private String documentType;

    /**
     * 文档分类（如：教务处、学生处等）
     */
    private String category;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 最后更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 是否已处理成向量（用于RAG）
     */
    private Boolean vectorized = false;

    /**
     * 处理状态（PENDING, PROCESSING, COMPLETED, FAILED）
     */
    private ProcessingStatus status = ProcessingStatus.PENDING;

    // 构造函数
    public KnowledgeDocument() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public KnowledgeDocument(String title, String content, String sourceUrl) {
        this();
        this.title = title;
        this.content = content;
        this.sourceUrl = sourceUrl;
    }

    // 更新时间的方法
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public Boolean getVectorized() {
        return vectorized;
    }

    public void setVectorized(Boolean vectorized) {
        this.vectorized = vectorized;
    }

    public ProcessingStatus getStatus() {
        return status;
    }

    public void setStatus(ProcessingStatus status) {
        this.status = status;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KnowledgeDocument that = (KnowledgeDocument) o;
        return Objects.equals(sourceUrl, that.sourceUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceUrl);
    }

    @Override
    public String toString() {
        return "KnowledgeDocument{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", sourceUrl='" + sourceUrl + '\'' +
                ", documentType='" + documentType + '\'' +
                ", category='" + category + '\'' +
                ", status=" + status +
                ", vectorized=" + vectorized +
                ", createdAt=" + createdAt +
                '}';
    }

    /**
     * 文档处理状态枚举
     */
    public enum ProcessingStatus {
        PENDING,    // 待处理
        PROCESSING, // 处理中
        COMPLETED,  // 已完成
        FAILED      // 处理失败
    }
}
