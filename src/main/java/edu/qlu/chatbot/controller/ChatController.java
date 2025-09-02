package edu.qlu.chatbot.controller;

import edu.qlu.chatbot.model.ChatRequest;
import edu.qlu.chatbot.model.ChatResponse;
import edu.qlu.chatbot.service.ChatService;
import edu.qlu.chatbot.service.DataCollectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 聊天机器人REST API控制器
 * 
 * 提供前端调用的HTTP接口：
 * - 聊天对话接口
 * - 会话管理接口
 * - 系统管理接口
 * 
 * @author AI Assistant
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final ChatService chatService;
    private final DataCollectionService dataCollectionService;

    public ChatController(ChatService chatService, DataCollectionService dataCollectionService) {
        this.chatService = chatService;
        this.dataCollectionService = dataCollectionService;
    }

    /**
     * 聊天接口
     * 
     * @param request 聊天请求
     * @return 聊天响应
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        logger.info("收到聊天请求: {}", request);
        
        try {
            ChatResponse response = chatService.chat(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("处理聊天请求失败", e);
            ChatResponse errorResponse = ChatResponse.error(
                "抱歉，我遇到了一些技术问题，请稍后再试。", 
                request.getConversationId()
            );
            return ResponseEntity.ok(errorResponse);
        }
    }

    /**
     * 获取会话历史
     * 
     * @param conversationId 会话ID
     * @return 会话历史
     */
    @GetMapping("/conversation/{conversationId}/history")
    public ResponseEntity<List<String>> getConversationHistory(@PathVariable String conversationId) {
        logger.info("获取会话历史: {}", conversationId);
        
        try {
            List<String> history = chatService.getConversationHistory(conversationId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            logger.error("获取会话历史失败", e);
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * 清除会话历史
     * 
     * @param conversationId 会话ID
     * @return 操作结果
     */
    @DeleteMapping("/conversation/{conversationId}")
    public ResponseEntity<Map<String, String>> clearConversationHistory(@PathVariable String conversationId) {
        logger.info("清除会话历史: {}", conversationId);
        
        try {
            chatService.clearConversationHistory(conversationId);
            return ResponseEntity.ok(Map.of("message", "会话历史已清除"));
        } catch (Exception e) {
            logger.error("清除会话历史失败", e);
            return ResponseEntity.ok(Map.of("error", "清除会话历史失败"));
        }
    }

    /**
     * 健康检查接口
     * 
     * @return 系统状态
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "QLU Academic Affairs Chatbot",
            "version", "1.0.0",
            "timestamp", System.currentTimeMillis()
        ));
    }

    /**
     * 系统信息接口
     * 
     * @return 系统信息
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        return ResponseEntity.ok(Map.of(
            "name", "齐鲁工业大学智能教务助手",
            "description", "基于Spring AI Alibaba构建的智能化教务机器人",
            "capabilities", List.of(
                "RAG知识库检索",
                "多轮对话记忆",
                "实时教务系统查询",
                "自然语言理解",
                "智能问答"
            ),
            "supportedQueries", List.of(
                "课程信息查询",
                "成绩查询",
                "考试安排",
                "图书馆信息",
                "校历查询",
                "规章制度咨询"
            )
        ));
    }

    /**
     * 触发数据采集（管理员接口）
     * 
     * @return 采集结果
     */
    @PostMapping("/admin/collect-data")
    public ResponseEntity<Map<String, String>> triggerDataCollection() {
        logger.info("手动触发数据采集");
        
        try {
            String result = dataCollectionService.collectData();
            return ResponseEntity.ok(Map.of("result", result));
        } catch (Exception e) {
            logger.error("数据采集失败", e);
            return ResponseEntity.ok(Map.of("error", "数据采集失败: " + e.getMessage()));
        }
    }

    /**
     * 错误处理
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        logger.error("API调用发生错误", e);
        return ResponseEntity.ok(Map.of(
            "error", "系统错误",
            "message", e.getMessage()
        ));
    }
}
