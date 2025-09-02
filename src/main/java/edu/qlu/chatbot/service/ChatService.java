package edu.qlu.chatbot.service;

import edu.qlu.chatbot.model.ChatRequest;
import edu.qlu.chatbot.model.ChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
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
            // 生成或使用现有的会话ID（在验证之前生成，确保所有响应都有ID）
            String conversationId = StringUtils.hasText(request.getConversationId()) 
                    ? request.getConversationId() 
                    : UUID.randomUUID().toString();

            // 验证输入
            if (!StringUtils.hasText(request.getMessage())) {
                return ChatResponse.error("消息不能为空", conversationId);
            }

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
            // 确保异常情况下也有conversationId
            String errorConversationId = StringUtils.hasText(request.getConversationId()) 
                    ? request.getConversationId() 
                    : UUID.randomUUID().toString();
            return ChatResponse.error("处理请求时发生错误: " + e.getMessage(), errorConversationId);
        }
    }

    /**
     * 获取对话历史记录
     */
    public List<String> getConversationHistory(String conversationId) {
        // 简化版本 - 返回空列表，实际项目中可以从数据库或缓存中获取
        logger.info("获取会话历史 - 会话ID: {}", conversationId);
        return new ArrayList<>();
    }

    /**
     * 清除对话历史记录
     */
    public void clearConversationHistory(String conversationId) {
        // 简化版本 - 仅记录日志，实际项目中可以清除数据库或缓存中的历史记录
        logger.info("清除会话历史 - 会话ID: {}", conversationId);
    }
}
