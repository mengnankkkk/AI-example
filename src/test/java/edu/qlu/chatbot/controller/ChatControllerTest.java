package edu.qlu.chatbot.controller;

import edu.qlu.chatbot.model.ChatRequest;
import edu.qlu.chatbot.model.ChatResponse;
import edu.qlu.chatbot.service.ChatService;
import edu.qlu.chatbot.service.DataCollectionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ChatController Mock测试类
 * 
 * 测试REST API接口的功能和异常处理
 * 
 * @author AI Assistant
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    @Mock
    private ChatService chatService;
    
    @Mock
    private DataCollectionService dataCollectionService;
    
    @InjectMocks
    private ChatController chatController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ChatRequest sampleRequest;
    private ChatResponse sampleResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(chatController).build();
        objectMapper = new ObjectMapper();

        // 准备测试数据
        sampleRequest = new ChatRequest();
        sampleRequest.setMessage("请问如何查询成绩？");
        sampleRequest.setConversationId("test-conversation-123");
        sampleRequest.setUserId("user-456");

        sampleResponse = new ChatResponse();
        sampleResponse.setResponse("您可以通过学生信息系统查询成绩...");
        sampleResponse.setConversationId("test-conversation-123");
        sampleResponse.setSuccess(true);
        sampleResponse.setTimestamp(System.currentTimeMillis());
    }

    @Test
    void testChatEndpoint_SuccessfulRequest() throws Exception {
        // Given
        when(chatService.chat(any(ChatRequest.class))).thenReturn(sampleResponse);

        // When & Then
        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.response").value("您可以通过学生信息系统查询成绩..."))
                .andExpect(jsonPath("$.conversationId").value("test-conversation-123"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(chatService).chat(any(ChatRequest.class));
    }

    @Test
    void testChatEndpoint_ServiceError() throws Exception {
        // Given - 模拟服务异常
        ChatResponse errorResponse = new ChatResponse();
        errorResponse.setSuccess(false);
        errorResponse.setError("服务暂时不可用");
        errorResponse.setConversationId("test-conversation-123");
        
        when(chatService.chat(any(ChatRequest.class))).thenReturn(errorResponse);

        // When & Then
        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("服务暂时不可用"))
                .andExpected(jsonPath("$.response").isEmpty());

        verify(chatService).chat(any(ChatRequest.class));
    }

    @Test
    void testChatEndpoint_InvalidRequest_EmptyMessage() throws Exception {
        // Given - 空消息请求
        ChatRequest invalidRequest = new ChatRequest();
        invalidRequest.setMessage("");
        invalidRequest.setConversationId("test-conversation-123");

        // When & Then
        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(chatService, never()).chat(any(ChatRequest.class));
    }

    @Test
    void testChatEndpoint_InvalidRequest_NullMessage() throws Exception {
        // Given - null消息请求
        ChatRequest invalidRequest = new ChatRequest();
        invalidRequest.setMessage(null);
        invalidRequest.setConversationId("test-conversation-123");

        // When & Then
        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpected(status().isBadRequest());

        verify(chatService, never()).chat(any(ChatRequest.class));
    }

    @Test
    void testChatEndpoint_InvalidJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
                .andExpected(status().isBadRequest());

        verify(chatService, never()).chat(any(ChatRequest.class));
    }

    @Test
    void testChatEndpoint_MissingContentType() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/chat")
                .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpected(status().isUnsupportedMediaType());

        verify(chatService, never()).chat(any(ChatRequest.class));
    }

    @Test
    void testHealthEndpoint() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/health"))
                .andExpected(status().isOk())
                .andExpected(content().contentType(MediaType.APPLICATION_JSON))
                .andExpected(jsonPath("$.status").value("UP"))
                .andExpected(jsonPath("$.service").value("QLU Academic Chatbot"))
                .andExpected(jsonPath("$.timestamp").exists());
    }

    @Test
    void testDataCollectionTriggerEndpoint() throws Exception {
        // Given
        doNothing().when(dataCollectionService).collectData();

        // When & Then
        mockMvc.perform(post("/api/admin/collect-data"))
                .andExpected(status().isOk())
                .andExpected(content().contentType(MediaType.APPLICATION_JSON))
                .andExpected(jsonPath("$.message").value("数据收集任务已启动"))
                .andExpected(jsonPath("$.timestamp").exists());

        verify(dataCollectionService).collectData();
    }

    @Test
    void testDataCollectionTriggerEndpoint_ServiceError() throws Exception {
        // Given - 模拟数据收集服务异常
        doThrow(new RuntimeException("数据收集失败"))
            .when(dataCollectionService).collectData();

        // When & Then
        mockMvc.perform(post("/api/admin/collect-data"))
                .andExpected(status().isInternalServerError())
                .andExpected(content().contentType(MediaType.APPLICATION_JSON))
                .andExpected(jsonPath("$.error").value("数据收集失败"))
                .andExpected(jsonPath("$.timestamp").exists());

        verify(dataCollectionService).collectData();
    }

    @Test
    void testChatEndpoint_LongMessage() throws Exception {
        // Given - 超长消息
        StringBuilder longMessage = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            longMessage.append("测试 ");
        }
        
        ChatRequest longRequest = new ChatRequest();
        longRequest.setMessage(longMessage.toString());
        longRequest.setConversationId("long-message-test");

        when(chatService.chat(any(ChatRequest.class))).thenReturn(sampleResponse);

        // When & Then
        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(longRequest)))
                .andExpected(status().isOk())
                .andExpected(jsonPath("$.success").value(true));

        verify(chatService).chat(any(ChatRequest.class));
    }

    @Test
    void testChatEndpoint_SpecialCharacters() throws Exception {
        // Given - 包含特殊字符的消息
        ChatRequest specialRequest = new ChatRequest();
        specialRequest.setMessage("测试消息 with emoji 😊 and symbols @#$%^&*()");
        specialRequest.setConversationId("special-chars-test");

        when(chatService.chat(any(ChatRequest.class))).thenReturn(sampleResponse);

        // When & Then
        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(specialRequest)))
                .andExpected(status().isOk())
                .andExpected(jsonPath("$.success").value(true));

        verify(chatService).chat(any(ChatRequest.class));
    }

    @Test
    void testChatEndpoint_ConcurrentRequests() throws Exception {
        // Given
        when(chatService.chat(any(ChatRequest.class))).thenReturn(sampleResponse);

        // When & Then - 模拟多个并发请求
        for (int i = 0; i < 5; i++) {
            ChatRequest request = new ChatRequest();
            request.setMessage("并发测试消息 " + i);
            request.setConversationId("concurrent-test-" + i);

            mockMvc.perform(post("/api/chat")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpected(status().isOk())
                    .andExpected(jsonPath("$.success").value(true));
        }

        verify(chatService, times(5)).chat(any(ChatRequest.class));
    }

    @Test
    void testCorsConfiguration() throws Exception {
        // When & Then - 测试CORS预检请求
        mockMvc.perform(options("/api/chat")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpected(status().isOk())
                .andExpected(header().string("Access-Control-Allow-Origin", "*"))
                .andExpected(header().string("Access-Control-Allow-Methods", 
                    containsString("POST")));
    }

    @Test
    void testErrorHandling_UnexpectedException() throws Exception {
        // Given - 模拟意外异常
        when(chatService.chat(any(ChatRequest.class)))
            .thenThrow(new RuntimeException("意外的系统错误"));

        // When & Then
        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpected(status().isInternalServerError())
                .andExpected(content().contentType(MediaType.APPLICATION_JSON))
                .andExpected(jsonPath("$.success").value(false))
                .andExpected(jsonPath("$.error").exists());

        verify(chatService).chat(any(ChatRequest.class));
    }

    @Test
    void testRequestValidation_MissingConversationId() throws Exception {
        // Given - 缺少会话ID
        ChatRequest invalidRequest = new ChatRequest();
        invalidRequest.setMessage("测试消息");
        // conversationId为null

        // When & Then
        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpected(status().isBadRequest());

        verify(chatService, never()).chat(any(ChatRequest.class));
    }

    @Test
    void testResponseHeaders() throws Exception {
        // Given
        when(chatService.chat(any(ChatRequest.class))).thenReturn(sampleResponse);

        // When & Then
        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpected(status().isOk())
                .andExpected(header().string("Content-Type", "application/json"))
                .andExpected(header().exists("Date"));

        verify(chatService).chat(any(ChatRequest.class));
    }

    @Test
    void testApiDocumentation() throws Exception {
        // 测试API文档相关的端点（如果有的话）
        mockMvc.perform(get("/api/docs")
                .accept(MediaType.APPLICATION_JSON))
                .andExpected(status().isNotFound()); // 假设文档端点不存在
    }
}
