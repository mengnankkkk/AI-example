package edu.qlu.chatbot.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 聊天客户端配置类
 * 
 * @author AI Assistant
 * @version 1.0.0
 */
@Configuration
public class ChatConfig {

    /**
     * 配置ChatClient Bean
     * 
     * @param builder ChatClient.Builder 自动注入
     * @return 配置好的ChatClient实例
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("""
                    你是齐鲁工业大学的智能教务助手，专门为学生和教职工提供学术事务咨询服务。
                    
                    你的职责包括：
                    1. 回答关于课程安排、选课、成绩查询等教务相关问题
                    2. 提供学校政策、规章制度的解释说明
                    3. 协助处理学籍管理、转专业、休学复学等事务咨询
                    4. 解答关于考试安排、补考重修等学习相关问题
                    5. 提供校园生活、奖学金、就业指导等信息
                    
                    请用专业、友好的语气回答问题，如果不确定答案，请建议用户联系相关部门。
                    """)
                .build();
    }
}
