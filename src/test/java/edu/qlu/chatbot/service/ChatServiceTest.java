package edu.qlu.chatbot.service;

import edu.qlu.chatbot.model.ChatRequest;
import edu.qlu.chatbot.model.ChatResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ChatService的简化单元测试
 * 
 * @author AI Assistant
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatClient chatClient;

    @InjectMocks
    private ChatService chatService;

    @Test
    void testChatWithEmptyMessage() {
        // 准备
        ChatRequest request = new ChatRequest();
        request.setMessage("");
        request.setConversationId("test-conversation-id");

        // 执行
        ChatResponse response = chatService.chat(request);

        // 验证
        assertNotNull(response);
        assertEquals(ChatResponse.ResponseStatus.ERROR, response.getStatus());
        assertEquals("消息不能为空", response.getError());
        assertEquals("test-conversation-id", response.getConversationId());
    }

    @Test
    void testChatWithNullMessage() {
        // 准备
        ChatRequest request = new ChatRequest();
        request.setMessage(null);
        request.setConversationId("test-conversation-id");

        // 执行
        ChatResponse response = chatService.chat(request);

        // 验证
        assertNotNull(response);
        assertEquals(ChatResponse.ResponseStatus.ERROR, response.getStatus());
        assertEquals("消息不能为空", response.getError());
    }

    @Test
    void testChatGeneratesConversationIdWhenNotProvided() {
        // 准备
        ChatRequest request = new ChatRequest();
        request.setMessage("");  // 使用空消息来避免ChatClient调用
        // 不设置conversationId

        // 执行
        ChatResponse response = chatService.chat(request);

        // 验证会话ID生成
        assertNotNull(response);
        assertNotNull(response.getConversationId());
        assertFalse(response.getConversationId().isEmpty());
    }

    @Test
    void testGetConversationHistory() {
        // 执行
        List<String> history = chatService.getConversationHistory("test-conversation-id");

        // 验证
        assertNotNull(history);
        assertTrue(history.isEmpty()); // 简化版本返回空列表
    }

    @Test
    void testClearConversationHistory() {
        // 执行（应该不抛出异常）
        assertDoesNotThrow(() -> {
            chatService.clearConversationHistory("test-conversation-id");
        });
    }
}
