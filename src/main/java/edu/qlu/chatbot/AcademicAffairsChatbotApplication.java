package edu.qlu.chatbot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 齐鲁工业大学智能教务机器人主应用类
 * 
 * 基于Spring AI Alibaba构建的智能化教务助手
 * 主要功能：
 * - RAG知识库检索
 * - 多轮对话记忆
 * - 函数调用支持
 * - 定时数据采集
 * 
 * @author AI Assistant
 * @version 1.0.0
 */
@SpringBootApplication
@EnableScheduling
@MapperScan("edu.qlu.chatbot.mapper")
public class AcademicAffairsChatbotApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcademicAffairsChatbotApplication.class, args);
    }

}
