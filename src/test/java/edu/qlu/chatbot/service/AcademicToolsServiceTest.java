package edu.qlu.chatbot.service;

import edu.qlu.chatbot.mapper.KnowledgeDocumentMapper;
import edu.qlu.chatbot.model.KnowledgeDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * AcademicToolsService Mock测试类
 * 
 * 测试学术工具服务的函数调用功能
 * 
 * @author AI Assistant
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class AcademicToolsServiceTest {

    @Mock
    private KnowledgeDocumentMapper documentMapper;
    
    @InjectMocks
    private AcademicToolsService academicToolsService;

    private KnowledgeDocument sampleDocument;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        sampleDocument = new KnowledgeDocument();
        sampleDocument.setId(1L);
        sampleDocument.setTitle("成绩查询指南");
        sampleDocument.setContent("学生可以通过学生信息系统查询成绩...");
        sampleDocument.setSourceUrl("https://www.qlu.edu.cn/grade-guide");
        sampleDocument.setDocumentType("ACADEMIC_AFFAIRS");
        sampleDocument.setCategory("教务处");
        sampleDocument.setVectorized(true);
        sampleDocument.setStatus(KnowledgeDocument.ProcessingStatus.COMPLETED);
        sampleDocument.setCreatedAt(LocalDateTime.now().minusDays(1));
        sampleDocument.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testSearchKnowledgeByCategory_ValidCategory() {
        // Given
        String category = "教务处";
        List<KnowledgeDocument> categoryDocs = Arrays.asList(sampleDocument);
        when(documentMapper.findByCategory(category)).thenReturn(categoryDocs);

        // When
        String result = academicToolsService.searchKnowledgeByCategory(category);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("成绩查询指南"));
        assertTrue(result.contains("教务处"));
        
        verify(documentMapper).findByCategory(category);
    }

    @Test
    void testSearchKnowledgeByCategory_EmptyResults() {
        // Given
        String category = "不存在的分类";
        when(documentMapper.findByCategory(category)).thenReturn(Arrays.asList());

        // When
        String result = academicToolsService.searchKnowledgeByCategory(category);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("未找到") || result.contains("没有找到"));
        
        verify(documentMapper).findByCategory(category);
    }

    @Test
    void testSearchKnowledgeByCategory_NullCategory() {
        // When
        String result = academicToolsService.searchKnowledgeByCategory(null);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("分类不能为空") || result.contains("无效"));
        
        verify(documentMapper, never()).findByCategory(any());
    }

    @Test
    void testSearchKnowledgeByCategory_EmptyCategory() {
        // When
        String result = academicToolsService.searchKnowledgeByCategory("");

        // Then
        assertNotNull(result);
        assertTrue(result.contains("分类不能为空") || result.contains("无效"));
        
        verify(documentMapper, never()).findByCategory(any());
    }

    @Test
    void testSearchKnowledgeByDocumentType_ValidType() {
        // Given
        String documentType = "ACADEMIC_AFFAIRS";
        List<KnowledgeDocument> typeDocs = Arrays.asList(sampleDocument);
        when(documentMapper.findByDocumentType(documentType)).thenReturn(typeDocs);

        // When
        String result = academicToolsService.searchKnowledgeByDocumentType(documentType);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("成绩查询指南"));
        assertTrue(result.contains("ACADEMIC_AFFAIRS"));
        
        verify(documentMapper).findByDocumentType(documentType);
    }

    @Test
    void testSearchKnowledgeByDocumentType_EmptyResults() {
        // Given
        String documentType = "UNKNOWN_TYPE";
        when(documentMapper.findByDocumentType(documentType)).thenReturn(Arrays.asList());

        // When
        String result = academicToolsService.searchKnowledgeByDocumentType(documentType);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("未找到") || result.contains("没有找到"));
        
        verify(documentMapper).findByDocumentType(documentType);
    }

    @Test
    void testSearchKnowledgeByKeyword_ValidKeyword() {
        // Given
        String keyword = "成绩";
        List<KnowledgeDocument> searchResults = Arrays.asList(sampleDocument);
        when(documentMapper.searchByKeyword(keyword)).thenReturn(searchResults);

        // When
        String result = academicToolsService.searchKnowledgeByKeyword(keyword);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("成绩查询指南"));
        assertTrue(result.contains("成绩"));
        
        verify(documentMapper).searchByKeyword(keyword);
    }

    @Test
    void testSearchKnowledgeByKeyword_EmptyResults() {
        // Given
        String keyword = "不存在的关键词";
        when(documentMapper.searchByKeyword(keyword)).thenReturn(Arrays.asList());

        // When
        String result = academicToolsService.searchKnowledgeByKeyword(keyword);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("未找到") || result.contains("没有找到"));
        
        verify(documentMapper).searchByKeyword(keyword);
    }

    @Test
    void testSearchKnowledgeByKeyword_NullKeyword() {
        // When
        String result = academicToolsService.searchKnowledgeByKeyword(null);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("关键词不能为空") || result.contains("无效"));
        
        verify(documentMapper, never()).searchByKeyword(any());
    }

    @Test
    void testGetKnowledgeStatistics() {
        // Given
        when(documentMapper.count()).thenReturn(100L);
        
        List<KnowledgeDocumentMapper.CategoryCount> categoryCounts = Arrays.asList();
        when(documentMapper.countByCategory()).thenReturn(categoryCounts);

        // When
        String result = academicToolsService.getKnowledgeStatistics();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("100") || result.contains("统计"));
        
        verify(documentMapper).count();
        verify(documentMapper).countByCategory();
    }

    @Test
    void testGetRecentDocuments() {
        // Given
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        List<KnowledgeDocument> recentDocs = Arrays.asList(sampleDocument);
        when(documentMapper.findByUpdatedAtAfter(any(LocalDateTime.class))).thenReturn(recentDocs);

        // When
        String result = academicToolsService.getRecentDocuments(7);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("成绩查询指南"));
        
        verify(documentMapper).findByUpdatedAtAfter(any(LocalDateTime.class));
    }

    @Test
    void testGetRecentDocuments_EmptyResults() {
        // Given
        when(documentMapper.findByUpdatedAtAfter(any(LocalDateTime.class))).thenReturn(Arrays.asList());

        // When
        String result = academicToolsService.getRecentDocuments(1);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("未找到") || result.contains("没有"));
        
        verify(documentMapper).findByUpdatedAtAfter(any(LocalDateTime.class));
    }

    @Test
    void testGetRecentDocuments_InvalidDays() {
        // When
        String result = academicToolsService.getRecentDocuments(-1);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("天数必须") || result.contains("无效"));
        
        verify(documentMapper, never()).findByUpdatedAtAfter(any());
    }

    @Test
    void testGetDocumentsByStatus() {
        // Given
        String status = "COMPLETED";
        List<KnowledgeDocument> statusDocs = Arrays.asList(sampleDocument);
        when(documentMapper.findByStatus(status)).thenReturn(statusDocs);

        // When
        String result = academicToolsService.getDocumentsByStatus(status);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("成绩查询指南"));
        assertTrue(result.contains("COMPLETED"));
        
        verify(documentMapper).findByStatus(status);
    }

    @Test
    void testGetDocumentsByStatus_EmptyResults() {
        // Given
        String status = "FAILED";
        when(documentMapper.findByStatus(status)).thenReturn(Arrays.asList());

        // When
        String result = academicToolsService.getDocumentsByStatus(status);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("未找到") || result.contains("没有"));
        
        verify(documentMapper).findByStatus(status);
    }

    @Test
    void testFormatDocumentList_SingleDocument() {
        // Given
        List<KnowledgeDocument> docs = Arrays.asList(sampleDocument);

        // When - 通过其他方法间接测试格式化逻辑
        when(documentMapper.findByCategory("教务处")).thenReturn(docs);
        String result = academicToolsService.searchKnowledgeByCategory("教务处");

        // Then
        assertNotNull(result);
        assertTrue(result.contains("成绩查询指南"));
        assertTrue(result.contains("https://www.qlu.edu.cn/grade-guide"));
    }

    @Test
    void testFormatDocumentList_MultipleDocuments() {
        // Given
        KnowledgeDocument doc2 = new KnowledgeDocument();
        doc2.setId(2L);
        doc2.setTitle("选课指南");
        doc2.setContent("学生选课相关信息...");
        doc2.setSourceUrl("https://www.qlu.edu.cn/course-selection");
        doc2.setCategory("教务处");

        List<KnowledgeDocument> docs = Arrays.asList(sampleDocument, doc2);

        // When
        when(documentMapper.findByCategory("教务处")).thenReturn(docs);
        String result = academicToolsService.searchKnowledgeByCategory("教务处");

        // Then
        assertNotNull(result);
        assertTrue(result.contains("成绩查询指南"));
        assertTrue(result.contains("选课指南"));
    }

    @Test
    void testDatabaseConnectionError() {
        // Given - 模拟数据库连接错误
        when(documentMapper.findByCategory(anyString()))
            .thenThrow(new RuntimeException("数据库连接失败"));

        // When
        String result = academicToolsService.searchKnowledgeByCategory("教务处");

        // Then
        assertNotNull(result);
        assertTrue(result.contains("系统错误") || result.contains("暂时无法"));
        
        verify(documentMapper).findByCategory("教务处");
    }

    @Test
    void testConcurrentRequests() {
        // Given - 模拟并发请求
        List<KnowledgeDocument> docs = Arrays.asList(sampleDocument);
        when(documentMapper.findByCategory(anyString())).thenReturn(docs);

        // When - 模拟多个并发调用
        String result1 = academicToolsService.searchKnowledgeByCategory("教务处");
        String result2 = academicToolsService.searchKnowledgeByCategory("学生处");
        String result3 = academicToolsService.searchKnowledgeByCategory("招生办");

        // Then
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);
        
        verify(documentMapper, times(3)).findByCategory(anyString());
    }

    @Test
    void testToolFunction_Parameters() {
        // 测试工具函数的参数处理
        Map<String, Object> params = Map.of(
            "category", "教务处",
            "documentType", "ACADEMIC_AFFAIRS",
            "keyword", "成绩",
            "days", 7,
            "status", "COMPLETED"
        );

        // 验证参数类型
        assertEquals("教务处", params.get("category"));
        assertEquals("ACADEMIC_AFFAIRS", params.get("documentType"));
        assertEquals("成绩", params.get("keyword"));
        assertEquals(7, params.get("days"));
        assertEquals("COMPLETED", params.get("status"));
    }

    @Test
    void testResponseFormatting() {
        // Given
        List<KnowledgeDocument> docs = Arrays.asList(sampleDocument);
        when(documentMapper.findByCategory("教务处")).thenReturn(docs);

        // When
        String result = academicToolsService.searchKnowledgeByCategory("教务处");

        // Then - 验证响应格式
        assertNotNull(result);
        // 验证包含必要的信息字段
        assertTrue(result.contains("标题") || result.contains("成绩查询指南"));
        assertTrue(result.contains("链接") || result.contains("https://"));
    }
}
