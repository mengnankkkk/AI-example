package edu.qlu.chatbot.service;

import edu.qlu.chatbot.model.KnowledgeDocument;
import edu.qlu.chatbot.mapper.KnowledgeDocumentMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 简化的数据采集服务类
 * 
 * @author AI Assistant
 * @version 1.0.0
 */
@Service
public class DataCollectionService {

    private static final Logger logger = LoggerFactory.getLogger(DataCollectionService.class);
    
    private final KnowledgeDocumentMapper knowledgeDocumentMapper;

    public DataCollectionService(KnowledgeDocumentMapper knowledgeDocumentMapper) {
        this.knowledgeDocumentMapper = knowledgeDocumentMapper;
    }

    /**
     * 根据URL采集单个页面的内容
     */
    @Transactional
    public void collectFromUrl(String url) {
        try {
            logger.info("开始采集URL: {}", url);
            
            // 使用Jsoup获取页面内容
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();

            // 提取标题
            String title = doc.title();
            if (!StringUtils.hasText(title)) {
                title = "未知标题";
            }

            // 提取正文内容
            String content = extractContent(doc);
            
            if (StringUtils.hasText(content)) {
                // 创建知识文档
                KnowledgeDocument knowledgeDoc = new KnowledgeDocument();
                knowledgeDoc.setTitle(title);
                knowledgeDoc.setContent(content);
                knowledgeDoc.setSourceUrl(url);
                knowledgeDoc.setDocumentType("网页");
                knowledgeDoc.setCategory("通用");
                knowledgeDoc.setCreatedAt(LocalDateTime.now());
                knowledgeDoc.setUpdatedAt(LocalDateTime.now());

                // 保存到数据库
                knowledgeDocumentMapper.insert(knowledgeDoc);
                
                logger.info("成功保存文档: {}", title);
            } else {
                logger.warn("未提取到有效内容: {}", url);
            }

        } catch (Exception e) {
            logger.error("采集URL失败: {} - {}", url, e.getMessage(), e);
        }
    }

    /**
     * 提取页面正文内容
     */
    private String extractContent(Document doc) {
        // 尝试多种选择器提取主要内容
        String[] selectors = {
            "article", ".content", ".main-content", "#content", 
            ".post-content", ".entry-content", "main", ".container"
        };
        
        for (String selector : selectors) {
            Elements elements = doc.select(selector);
            if (!elements.isEmpty()) {
                String text = elements.first().text();
                if (StringUtils.hasText(text) && text.length() > 100) {
                    return text;
                }
            }
        }
        
        // 如果没有找到合适的选择器，使用body
        return doc.body().text();
    }

    /**
     * 获取所有文档数量
     */
    public long getTotalDocumentCount() {
        return knowledgeDocumentMapper.count();
    }

    /**
     * 执行数据采集（简化版本）
     */
    public String collectData() {
        try {
            logger.info("开始执行数据采集");
            
            // 这里可以添加实际的数据采集逻辑
            // 例如：采集学校官网的一些固定页面
            String[] urls = {
                "https://www.qlu.edu.cn/",
                "https://www.qlu.edu.cn/jwc/",
                "https://www.qlu.edu.cn/xsc/"
            };
            
            int collected = 0;
            for (String url : urls) {
                try {
                    collectFromUrl(url);
                    collected++;
                } catch (Exception e) {
                    logger.warn("采集URL失败: {} - {}", url, e.getMessage());
                }
            }
            
            String result = String.format("数据采集完成，成功采集 %d 个页面", collected);
            logger.info(result);
            return result;
            
        } catch (Exception e) {
            String error = "数据采集失败: " + e.getMessage();
            logger.error(error, e);
            return error;
        }
    }
    public List<KnowledgeDocument> searchDocuments(String keyword, int limit) {
        if (!StringUtils.hasText(keyword)) {
            // 如果没有关键词，返回最新的文档（通过分页实现）
            return knowledgeDocumentMapper.findPendingDocuments(limit);
        }
        // 搜索关键词，然后手动限制结果数量
        List<KnowledgeDocument> results = knowledgeDocumentMapper.searchByKeyword(keyword);
        return results.stream().limit(limit).collect(java.util.stream.Collectors.toList());
    }
}
