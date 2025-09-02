package edu.qlu.chatbot.service;

import edu.qlu.chatbot.model.ChatRequest;
import edu.qlu.chatbot.model.ChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * 简化的聊天服务类
 * 
 * @author AI Assistant
 * @version 1.0.0
 */
@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    private final ChatClient chatClient;

    public ChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * 处理聊天请求
     * 
     * @param request 聊天请求
     * @return 聊天响应
     */
    public ChatResponse chat(ChatRequest request) {
        try {
            // 验证输入
            if (!StringUtils.hasText(request.getMessage())) {
                return ChatResponse.error("消息不能为空", request.getConversationId());
            }

            // 生成或使用现有的会话ID
            String conversationId = StringUtils.hasText(request.getConversationId()) 
                    ? request.getConversationId() 
                    : UUID.randomUUID().toString();

            logger.info("处理聊天请求 - 会话ID: {}, 消息: {}", conversationId, request.getMessage());

            // 调用ChatClient获取响应
            String response = chatClient
                    .prompt()
                    .user(request.getMessage())
                    .call()
                    .content();

            logger.info("ChatClient响应成功 - 会话ID: {}", conversationId);
            
            return ChatResponse.success(response, conversationId);

        } catch (Exception e) {
            logger.error("处理聊天请求时发生错误: {}", e.getMessage(), e);
            return ChatResponse.error("处理请求时发生错误: " + e.getMessage(), 
                                    request.getConversationId());
        }
    }
}
