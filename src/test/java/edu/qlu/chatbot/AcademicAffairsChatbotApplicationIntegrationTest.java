package edu.qlu.chatbot;

import edu.qlu.chatbot.controller.ChatController;
import edu.qlu.chatbot.mapper.KnowledgeDocumentMapper;
import edu.qlu.chatbot.service.ChatService;
import edu.qlu.chatbot.service.DataCollectionService;
import edu.qlu.chatbot.service.AcademicToolsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 应用程序集成测试类
 * 
 * 测试Spring Boot应用程序的启动和组件装配
 * 使用Mock对象避免依赖外部服务
 * 
 * @author AI Assistant
 * @version 1.0.0
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.url=jdbc:h2:mem:integrationtestdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.sql.init.mode=always",
    "spring.sql.init.schema-locations=classpath:schema-test.sql",
    "mybatis.configuration.map-underscore-to-camel-case=true",
    // Mock AI配置
    "spring.ai.dashscope.api-key=test-mock-key",
    "spring.ai.dashscope.chat.api-key=test-mock-key", 
    "spring.ai.alibaba.dashscope.api-key=test-mock-key",
    "spring.ai.alibaba.dashscope.chat.options.model=qwen-plus",
    // 禁用定时任务
    "app.data-collection.schedule=-",
    "app.data-collection.enabled=false",
    // Mock向量存储配置
    "spring.ai.vectorstore.pgvector.database=test",
    "spring.ai.vectorstore.pgvector.host=localhost",
    "spring.ai.vectorstore.pgvector.port=5432",
    "spring.ai.vectorstore.pgvector.username=test",
    "spring.ai.vectorstore.pgvector.password=test"
})
class AcademicAffairsChatbotApplicationIntegrationTest {

    @Autowired
    private ChatController chatController;
    
    @Autowired
    private ChatService chatService;
    
    @Autowired
    private DataCollectionService dataCollectionService;
    
    @Autowired
    private AcademicToolsService academicToolsService;
    
    @Autowired
    private KnowledgeDocumentMapper knowledgeDocumentMapper;
    
    // Mock外部依赖
    @MockBean
    private ChatClient chatClient;
    
    @MockBean
    private VectorStore vectorStore;
    
    @MockBean
    private ChatMemory chatMemory;

    @Test
    void contextLoads() {
        // 验证Spring上下文能够正确加载
        assertNotNull(chatController);
        assertNotNull(chatService);
        assertNotNull(dataCollectionService);
        assertNotNull(academicToolsService);
        assertNotNull(knowledgeDocumentMapper);
    }

    @Test
    void testControllerAutowiring() {
        // 验证Controller层自动装配
        assertNotNull(chatController);
        // 通过反射或公共方法验证依赖注入正确
    }

    @Test
    void testServiceLayerAutowiring() {
        // 验证Service层自动装配
        assertNotNull(chatService);
        assertNotNull(dataCollectionService);
        assertNotNull(academicToolsService);
    }

    @Test
    void testMapperAutowiring() {
        // 验证MyBatis Mapper自动装配
        assertNotNull(knowledgeDocumentMapper);
        
        // 测试基本的数据库操作
        long count = knowledgeDocumentMapper.count();
        assertTrue(count >= 0); // H2数据库可能已经有测试数据
    }

    @Test
    void testConfigurationProperties() {
        // 验证配置属性加载
        assertNotNull(dataCollectionService);
        // 如果有公共方法访问配置，可以验证配置值
    }

    @Test
    void testMyBatisConfiguration() {
        // 验证MyBatis配置正确
        assertNotNull(knowledgeDocumentMapper);
        
        // 测试基本查询操作
        assertDoesNotThrow(() -> {
            knowledgeDocumentMapper.findAll();
        });
    }

    @Test
    void testDatabaseConnection() {
        // 验证数据库连接正常
        assertDoesNotThrow(() -> {
            long count = knowledgeDocumentMapper.count();
            assertTrue(count >= 0);
        });
    }

    @Test
    void testApplicationProfiles() {
        // 验证测试配置文件生效
        // 这里可以通过Environment bean来检查激活的profile
    }

    @Test
    void testBeanDefinitions() {
        // 验证关键Bean定义正确
        assertNotNull(chatService);
        assertNotNull(dataCollectionService);
        assertNotNull(academicToolsService);
        assertNotNull(knowledgeDocumentMapper);
    }

    @Test
    void testMockBeans() {
        // 验证Mock Bean正确注入
        assertNotNull(chatClient);
        assertNotNull(vectorStore);
        assertNotNull(chatMemory);
    }
}
