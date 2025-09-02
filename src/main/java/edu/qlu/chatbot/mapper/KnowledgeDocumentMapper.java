package edu.qlu.chatbot.mapper;

import edu.qlu.chatbot.model.KnowledgeDocument;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 知识文档MyBatis Mapper
 * 
 * 提供对KnowledgeDocument实体的数据库操作
 * 
 * @author AI Assistant
 * @version 1.0.0
 */
@Mapper
public interface KnowledgeDocumentMapper {

    /**
     * 插入新文档
     */
    @Insert("""
        INSERT INTO knowledge_documents 
        (title, content, source_url, document_type, category, created_at, updated_at, vectorized, status)
        VALUES (#{title}, #{content}, #{sourceUrl}, #{documentType}, #{category}, 
                #{createdAt}, #{updatedAt}, #{vectorized}, #{status})
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(KnowledgeDocument document);

    /**
     * 根据ID查找文档
     */
    @Select("SELECT * FROM knowledge_documents WHERE id = #{id}")
    @Results({
        @Result(property = "sourceUrl", column = "source_url"),
        @Result(property = "documentType", column = "document_type"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    KnowledgeDocument findById(Long id);

    /**
     * 根据源URL查找文档
     */
    @Select("SELECT * FROM knowledge_documents WHERE source_url = #{sourceUrl}")
    @Results({
        @Result(property = "sourceUrl", column = "source_url"),
        @Result(property = "documentType", column = "document_type"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    KnowledgeDocument findBySourceUrl(String sourceUrl);

    /**
     * 检查URL是否已存在
     */
    @Select("SELECT COUNT(*) FROM knowledge_documents WHERE source_url = #{sourceUrl}")
    boolean existsBySourceUrl(String sourceUrl);

    /**
     * 根据文档类型查找文档
     */
    @Select("SELECT * FROM knowledge_documents WHERE document_type = #{documentType}")
    @Results({
        @Result(property = "sourceUrl", column = "source_url"),
        @Result(property = "documentType", column = "document_type"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    List<KnowledgeDocument> findByDocumentType(String documentType);

    /**
     * 根据分类查找文档
     */
    @Select("SELECT * FROM knowledge_documents WHERE category = #{category}")
    @Results({
        @Result(property = "sourceUrl", column = "source_url"),
        @Result(property = "documentType", column = "document_type"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    List<KnowledgeDocument> findByCategory(String category);

    /**
     * 查找未向量化的文档
     */
    @Select("SELECT * FROM knowledge_documents WHERE vectorized = false AND status = 'COMPLETED'")
    @Results({
        @Result(property = "sourceUrl", column = "source_url"),
        @Result(property = "documentType", column = "document_type"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    List<KnowledgeDocument> findUnvectorizedDocuments();

    /**
     * 根据处理状态查找文档
     */
    @Select("SELECT * FROM knowledge_documents WHERE status = #{status}")
    @Results({
        @Result(property = "sourceUrl", column = "source_url"),
        @Result(property = "documentType", column = "document_type"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    List<KnowledgeDocument> findByStatus(String status);

    /**
     * 查找最近更新的文档
     */
    @Select("SELECT * FROM knowledge_documents WHERE updated_at > #{since}")
    @Results({
        @Result(property = "sourceUrl", column = "source_url"),
        @Result(property = "documentType", column = "document_type"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    List<KnowledgeDocument> findByUpdatedAtAfter(LocalDateTime since);

    /**
     * 按分类统计文档数量
     */
    @Select("SELECT category, COUNT(*) as count FROM knowledge_documents GROUP BY category")
    List<CategoryCount> countByCategory();

    /**
     * 根据关键词搜索文档（标题和内容）
     */
    @Select("""
        SELECT * FROM knowledge_documents 
        WHERE LOWER(title) LIKE LOWER(CONCAT('%', #{keyword}, '%')) 
           OR LOWER(content) LIKE LOWER(CONCAT('%', #{keyword}, '%'))
        """)
    @Results({
        @Result(property = "sourceUrl", column = "source_url"),
        @Result(property = "documentType", column = "document_type"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    List<KnowledgeDocument> searchByKeyword(String keyword);

    /**
     * 查找待处理的文档（用于批量处理）
     */
    @Select("SELECT * FROM knowledge_documents WHERE status = 'PENDING' ORDER BY created_at ASC LIMIT #{limit}")
    @Results({
        @Result(property = "sourceUrl", column = "source_url"),
        @Result(property = "documentType", column = "document_type"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    List<KnowledgeDocument> findPendingDocuments(int limit);

    /**
     * 更新文档
     */
    @Update("""
        UPDATE knowledge_documents 
        SET title = #{title}, content = #{content}, document_type = #{documentType}, 
            category = #{category}, updated_at = #{updatedAt}, vectorized = #{vectorized}, 
            status = #{status}
        WHERE id = #{id}
        """)
    int update(KnowledgeDocument document);

    /**
     * 更新向量化状态
     */
    @Update("UPDATE knowledge_documents SET vectorized = #{vectorized}, updated_at = NOW() WHERE id = #{id}")
    int updateVectorizedStatus(@Param("id") Long id, @Param("vectorized") Boolean vectorized);

    /**
     * 删除文档
     */
    @Delete("DELETE FROM knowledge_documents WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 删除指定时间之前的旧文档
     */
    @Delete("DELETE FROM knowledge_documents WHERE created_at < #{cutoffDate}")
    int deleteOldDocuments(LocalDateTime cutoffDate);

    /**
     * 获取所有文档
     */
    @Select("SELECT * FROM knowledge_documents ORDER BY created_at DESC")
    @Results({
        @Result(property = "sourceUrl", column = "source_url"),
        @Result(property = "documentType", column = "document_type"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    List<KnowledgeDocument> findAll();

    /**
     * 统计总文档数
     */
    @Select("SELECT COUNT(*) FROM knowledge_documents")
    long count();

    /**
     * 分类统计内部类
     */
    class CategoryCount {
        private String category;
        private Long count;

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public Long getCount() { return count; }
        public void setCount(Long count) { this.count = count; }
    }
}
