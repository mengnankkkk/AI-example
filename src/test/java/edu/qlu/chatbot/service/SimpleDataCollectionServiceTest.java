package edu.qlu.chatbot.service;

import edu.qlu.chatbot.mapper.KnowledgeDocumentMapper;
import edu.qlu.chatbot.model.KnowledgeDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * DataCollectionService的简化单元测试
 * 
 * @author AI Assistant
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class DataCollectionServiceTest {

    @Mock
    private KnowledgeDocumentMapper knowledgeDocumentMapper;

    @InjectMocks
    private DataCollectionService dataCollectionService;

    @BeforeEach
    void setUp() {
        // 基础设置
    }

    @Test
    void testGetTotalDocumentCount() {
        // 准备
        when(knowledgeDocumentMapper.count()).thenReturn(100L);

        // 执行
        long count = dataCollectionService.getTotalDocumentCount();

        // 验证
        assertEquals(100L, count);
        verify(knowledgeDocumentMapper).count();
    }

    @Test
    void testSearchDocumentsWithKeyword() {
        // 准备
        String keyword = "测试";
        int limit = 10;
        List<KnowledgeDocument> mockResults = Arrays.asList(
            createMockDocument("文档1", "内容1"),
            createMockDocument("文档2", "内容2")
        );
        when(knowledgeDocumentMapper.searchByKeyword(keyword)).thenReturn(mockResults);

        // 执行
        List<KnowledgeDocument> results = dataCollectionService.searchDocuments(keyword, limit);

        // 验证
        assertNotNull(results);
        assertEquals(2, results.size());
        verify(knowledgeDocumentMapper).searchByKeyword(keyword);
    }

    @Test
    void testSearchDocumentsWithoutKeyword() {
        // 准备
        int limit = 5;
        List<KnowledgeDocument> mockResults = Arrays.asList(
            createMockDocument("文档1", "内容1")
        );
        when(knowledgeDocumentMapper.findPendingDocuments(limit)).thenReturn(mockResults);

        // 执行
        List<KnowledgeDocument> results = dataCollectionService.searchDocuments("", limit);

        // 验证
        assertNotNull(results);
        assertEquals(1, results.size());
        verify(knowledgeDocumentMapper).findPendingDocuments(limit);
    }

    @Test
    void testCollectData() {
        // 执行
        String result = dataCollectionService.collectData();

        // 验证
        assertNotNull(result);
        assertTrue(result.contains("数据采集"));
    }

    private KnowledgeDocument createMockDocument(String title, String content) {
        KnowledgeDocument doc = new KnowledgeDocument();
        doc.setTitle(title);
        doc.setContent(content);
        doc.setSourceUrl("http://test.com");
        return doc;
    }
}
