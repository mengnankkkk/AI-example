package edu.qlu.chatbot.mapper;

import edu.qlu.chatbot.model.KnowledgeDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * KnowledgeDocumentMapper 集成测试类
 * 
 * 使用H2内存数据库进行测试，不依赖实际的PostgreSQL环境
 * 
 * @author AI Assistant
 * @version 1.0.0
 */
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.sql.init.mode=always",
    "spring.sql.init.schema-locations=classpath:schema-test.sql",
    "mybatis.configuration.map-underscore-to-camel-case=true"
})
@SpringJUnitConfig
class KnowledgeDocumentMapperTest {

    @Autowired
    private KnowledgeDocumentMapper mapper;

    private KnowledgeDocument sampleDocument;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        sampleDocument = new KnowledgeDocument();
        sampleDocument.setTitle("测试文档");
        sampleDocument.setContent("这是一个测试文档的内容");
        sampleDocument.setSourceUrl("https://www.qlu.edu.cn/test");
        sampleDocument.setDocumentType("ACADEMIC_AFFAIRS");
        sampleDocument.setCategory("教务处");
        sampleDocument.setVectorized(false);
        sampleDocument.setStatus(KnowledgeDocument.ProcessingStatus.COMPLETED);
        sampleDocument.updateTimestamp();
    }

    @Test
    void testInsertAndFindById() {
        // When - 插入文档
        int result = mapper.insert(sampleDocument);
        
        // Then
        assertEquals(1, result);
        assertNotNull(sampleDocument.getId());
        
        // When - 根据ID查找
        KnowledgeDocument found = mapper.findById(sampleDocument.getId());
        
        // Then
        assertNotNull(found);
        assertEquals(sampleDocument.getTitle(), found.getTitle());
        assertEquals(sampleDocument.getContent(), found.getContent());
        assertEquals(sampleDocument.getSourceUrl(), found.getSourceUrl());
        assertEquals(sampleDocument.getDocumentType(), found.getDocumentType());
        assertEquals(sampleDocument.getCategory(), found.getCategory());
    }

    @Test
    void testFindBySourceUrl() {
        // Given
        mapper.insert(sampleDocument);
        
        // When
        KnowledgeDocument found = mapper.findBySourceUrl(sampleDocument.getSourceUrl());
        
        // Then
        assertNotNull(found);
        assertEquals(sampleDocument.getSourceUrl(), found.getSourceUrl());
        assertEquals(sampleDocument.getTitle(), found.getTitle());
    }

    @Test
    void testExistsBySourceUrl() {
        // Given
        mapper.insert(sampleDocument);
        
        // When & Then
        assertTrue(mapper.existsBySourceUrl(sampleDocument.getSourceUrl()));
        assertFalse(mapper.existsBySourceUrl("https://nonexistent.url"));
    }

    @Test
    void testFindByDocumentType() {
        // Given
        mapper.insert(sampleDocument);
        
        // When
        List<KnowledgeDocument> found = mapper.findByDocumentType("ACADEMIC_AFFAIRS");
        
        // Then
        assertNotNull(found);
        assertEquals(1, found.size());
        assertEquals("ACADEMIC_AFFAIRS", found.get(0).getDocumentType());
    }

    @Test
    void testFindByCategory() {
        // Given
        mapper.insert(sampleDocument);
        
        // When
        List<KnowledgeDocument> found = mapper.findByCategory("教务处");
        
        // Then
        assertNotNull(found);
        assertEquals(1, found.size());
        assertEquals("教务处", found.get(0).getCategory());
    }

    @Test
    void testFindUnvectorizedDocuments() {
        // Given
        mapper.insert(sampleDocument);
        
        // When
        List<KnowledgeDocument> unvectorized = mapper.findUnvectorizedDocuments();
        
        // Then
        assertNotNull(unvectorized);
        assertEquals(1, unvectorized.size());
        assertFalse(unvectorized.get(0).isVectorized());
        assertEquals(KnowledgeDocument.ProcessingStatus.COMPLETED, 
                    unvectorized.get(0).getStatus());
    }

    @Test
    void testFindByStatus() {
        // Given
        mapper.insert(sampleDocument);
        
        // When
        List<KnowledgeDocument> completed = 
            mapper.findByStatus(KnowledgeDocument.ProcessingStatus.COMPLETED.name());
        
        // Then
        assertNotNull(completed);
        assertEquals(1, completed.size());
        assertEquals(KnowledgeDocument.ProcessingStatus.COMPLETED.name(), 
                    completed.get(0).getStatus().name());
    }

    @Test
    void testSearchByKeyword() {
        // Given
        mapper.insert(sampleDocument);
        
        // When
        List<KnowledgeDocument> searchResults = mapper.searchByKeyword("测试");
        
        // Then
        assertNotNull(searchResults);
        assertEquals(1, searchResults.size());
        assertTrue(searchResults.get(0).getTitle().contains("测试"));
    }

    @Test
    void testUpdate() {
        // Given
        mapper.insert(sampleDocument);
        Long id = sampleDocument.getId();
        
        // When - 更新文档
        sampleDocument.setTitle("更新后的标题");
        sampleDocument.setContent("更新后的内容");
        sampleDocument.updateTimestamp();
        
        int updateResult = mapper.update(sampleDocument);
        
        // Then
        assertEquals(1, updateResult);
        
        KnowledgeDocument updated = mapper.findById(id);
        assertEquals("更新后的标题", updated.getTitle());
        assertEquals("更新后的内容", updated.getContent());
    }

    @Test
    void testUpdateVectorizedStatus() {
        // Given
        mapper.insert(sampleDocument);
        Long id = sampleDocument.getId();
        
        // When
        int updateResult = mapper.updateVectorizedStatus(id, true);
        
        // Then
        assertEquals(1, updateResult);
        
        KnowledgeDocument updated = mapper.findById(id);
        assertTrue(updated.isVectorized());
    }

    @Test
    void testDeleteById() {
        // Given
        mapper.insert(sampleDocument);
        Long id = sampleDocument.getId();
        
        // When
        int deleteResult = mapper.deleteById(id);
        
        // Then
        assertEquals(1, deleteResult);
        
        KnowledgeDocument deleted = mapper.findById(id);
        assertNull(deleted);
    }

    @Test
    void testCount() {
        // Given
        mapper.insert(sampleDocument);
        
        // When
        long count = mapper.count();
        
        // Then
        assertEquals(1, count);
    }

    @Test
    void testFindAll() {
        // Given
        mapper.insert(sampleDocument);
        
        // When
        List<KnowledgeDocument> allDocs = mapper.findAll();
        
        // Then
        assertNotNull(allDocs);
        assertEquals(1, allDocs.size());
    }

    @Test
    void testFindByUpdatedAtAfter() {
        // Given
        mapper.insert(sampleDocument);
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        
        // When
        List<KnowledgeDocument> recentDocs = mapper.findByUpdatedAtAfter(yesterday);
        
        // Then
        assertNotNull(recentDocs);
        assertEquals(1, recentDocs.size());
    }

    @Test
    void testFindPendingDocuments() {
        // Given - 创建PENDING状态的文档
        KnowledgeDocument pendingDoc = new KnowledgeDocument();
        pendingDoc.setTitle("待处理文档");
        pendingDoc.setContent("待处理内容");
        pendingDoc.setSourceUrl("https://www.qlu.edu.cn/pending");
        pendingDoc.setDocumentType("ACADEMIC_AFFAIRS");
        pendingDoc.setCategory("教务处");
        pendingDoc.setStatus(KnowledgeDocument.ProcessingStatus.PENDING);
        pendingDoc.updateTimestamp();
        
        mapper.insert(pendingDoc);
        
        // When
        List<KnowledgeDocument> pendingDocs = mapper.findPendingDocuments(10);
        
        // Then
        assertNotNull(pendingDocs);
        assertEquals(1, pendingDocs.size());
        assertEquals(KnowledgeDocument.ProcessingStatus.PENDING, 
                    pendingDocs.get(0).getStatus());
    }

    @Test
    void testDeleteOldDocuments() {
        // Given
        mapper.insert(sampleDocument);
        LocalDateTime future = LocalDateTime.now().plusDays(1);
        
        // When
        int deleteResult = mapper.deleteOldDocuments(future);
        
        // Then
        assertEquals(1, deleteResult);
        assertEquals(0, mapper.count());
    }

    @Test
    void testCountByCategory() {
        // Given
        mapper.insert(sampleDocument);
        
        // When
        List<KnowledgeDocumentMapper.CategoryCount> categoryCounts = 
            mapper.countByCategory();
        
        // Then
        assertNotNull(categoryCounts);
        assertEquals(1, categoryCounts.size());
        assertEquals("教务处", categoryCounts.get(0).getCategory());
        assertEquals(1L, categoryCounts.get(0).getCount());
    }

    @Test
    void testMultipleDocuments() {
        // Given - 插入多个文档
        KnowledgeDocument doc1 = sampleDocument;
        mapper.insert(doc1);
        
        KnowledgeDocument doc2 = new KnowledgeDocument();
        doc2.setTitle("第二个文档");
        doc2.setContent("第二个文档的内容");
        doc2.setSourceUrl("https://www.qlu.edu.cn/test2");
        doc2.setDocumentType("STUDENT_AFFAIRS");
        doc2.setCategory("学生处");
        doc2.setStatus(KnowledgeDocument.ProcessingStatus.COMPLETED);
        doc2.updateTimestamp();
        mapper.insert(doc2);
        
        // When
        List<KnowledgeDocument> allDocs = mapper.findAll();
        
        // Then
        assertEquals(2, allDocs.size());
        assertEquals(2, mapper.count());
        
        // 测试按分类查找
        List<KnowledgeDocument> doc1Category = mapper.findByCategory("教务处");
        List<KnowledgeDocument> doc2Category = mapper.findByCategory("学生处");
        
        assertEquals(1, doc1Category.size());
        assertEquals(1, doc2Category.size());
    }

    @Test
    void testErrorHandling() {
        // 测试插入重复URL
        mapper.insert(sampleDocument);
        
        // 尝试插入相同URL的文档应该失败或者通过业务逻辑处理
        assertTrue(mapper.existsBySourceUrl(sampleDocument.getSourceUrl()));
    }
}
