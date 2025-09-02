package edu.qlu.chatbot.repository;

import edu.qlu.chatbot.model.KnowledgeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 知识文档数据访问层
 * 
 * 提供对KnowledgeDocument实体的数据库操作
 * 
 * @author AI Assistant
 * @version 1.0.0
 */
@Repository
public interface KnowledgeDocumentRepository extends JpaRepository<KnowledgeDocument, Long> {

    /**
     * 根据源URL查找文档
     * 
     * @param sourceUrl 源URL
     * @return 可能存在的文档
     */
    Optional<KnowledgeDocument> findBySourceUrl(String sourceUrl);

    /**
     * 检查URL是否已存在
     * 
     * @param sourceUrl 源URL
     * @return 是否存在
     */
    boolean existsBySourceUrl(String sourceUrl);

    /**
     * 根据文档类型查找文档
     * 
     * @param documentType 文档类型
     * @return 文档列表
     */
    List<KnowledgeDocument> findByDocumentType(String documentType);

    /**
     * 根据分类查找文档
     * 
     * @param category 分类
     * @return 文档列表
     */
    List<KnowledgeDocument> findByCategory(String category);

    /**
     * 查找未向量化的文档
     * 
     * @return 未向量化的文档列表
     */
    @Query("SELECT kd FROM KnowledgeDocument kd WHERE kd.vectorized = false AND kd.status = 'COMPLETED'")
    List<KnowledgeDocument> findUnvectorizedDocuments();

    /**
     * 根据处理状态查找文档
     * 
     * @param status 处理状态
     * @return 文档列表
     */
    List<KnowledgeDocument> findByStatus(KnowledgeDocument.ProcessingStatus status);

    /**
     * 查找最近更新的文档
     * 
     * @param since 时间点
     * @return 文档列表
     */
    List<KnowledgeDocument> findByUpdatedAtAfter(LocalDateTime since);

    /**
     * 按分类统计文档数量
     * 
     * @return 分类统计结果
     */
    @Query("SELECT kd.category, COUNT(kd) FROM KnowledgeDocument kd GROUP BY kd.category")
    List<Object[]> countByCategory();

    /**
     * 根据关键词搜索文档（标题和内容）
     * 
     * @param keyword 关键词
     * @return 包含关键词的文档列表
     */
    @Query("SELECT kd FROM KnowledgeDocument kd WHERE " +
           "LOWER(kd.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(kd.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<KnowledgeDocument> searchByKeyword(@Param("keyword") String keyword);

    /**
     * 查找待处理的文档（用于批量处理）
     * 
     * @param limit 限制数量
     * @return 待处理的文档列表
     */
    @Query("SELECT kd FROM KnowledgeDocument kd WHERE kd.status = 'PENDING' ORDER BY kd.createdAt ASC")
    List<KnowledgeDocument> findPendingDocuments(@Param("limit") int limit);

    /**
     * 删除指定时间之前的旧文档
     * 
     * @param cutoffDate 截止日期
     * @return 删除的文档数量
     */
    @Query("DELETE FROM KnowledgeDocument kd WHERE kd.createdAt < :cutoffDate")
    int deleteOldDocuments(@Param("cutoffDate") LocalDateTime cutoffDate);
}
