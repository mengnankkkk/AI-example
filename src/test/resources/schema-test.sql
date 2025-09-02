-- H2 测试数据库初始化脚本
-- 为KnowledgeDocument创建表结构

DROP TABLE IF EXISTS knowledge_documents;

CREATE TABLE knowledge_documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    content TEXT NOT NULL,
    source_url VARCHAR(1000) UNIQUE NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    category VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    vectorized BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) DEFAULT 'PENDING'
);

-- 创建索引以提高查询性能
CREATE INDEX idx_knowledge_documents_source_url ON knowledge_documents(source_url);
CREATE INDEX idx_knowledge_documents_document_type ON knowledge_documents(document_type);
CREATE INDEX idx_knowledge_documents_category ON knowledge_documents(category);
CREATE INDEX idx_knowledge_documents_status ON knowledge_documents(status);
CREATE INDEX idx_knowledge_documents_vectorized ON knowledge_documents(vectorized);
CREATE INDEX idx_knowledge_documents_updated_at ON knowledge_documents(updated_at);
CREATE INDEX idx_knowledge_documents_created_at ON knowledge_documents(created_at);

-- 插入一些测试数据（可选）
INSERT INTO knowledge_documents (title, content, source_url, document_type, category, vectorized, status) VALUES
('测试文档1', '这是第一个测试文档的内容', 'https://www.qlu.edu.cn/test1', 'ACADEMIC_AFFAIRS', '教务处', false, 'COMPLETED'),
('测试文档2', '这是第二个测试文档的内容', 'https://www.qlu.edu.cn/test2', 'STUDENT_AFFAIRS', '学生处', true, 'COMPLETED'),
('待处理文档', '这是一个待处理的文档', 'https://www.qlu.edu.cn/pending', 'ACADEMIC_AFFAIRS', '教务处', false, 'PENDING');
