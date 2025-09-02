# =============================================
# 齐鲁工业大学智能教务机器人数据库初始化脚本
# =============================================

# 1. 创建数据库
CREATE DATABASE qlu_chatbot 
    WITH ENCODING 'UTF8' 
    LC_COLLATE='zh_CN.UTF-8' 
    LC_CTYPE='zh_CN.UTF-8';

# 2. 连接到数据库
\c qlu_chatbot;

# 3. 安装pgvector扩展
CREATE EXTENSION IF NOT EXISTS vector;

# 4. 创建知识文档表
CREATE TABLE IF NOT EXISTS knowledge_documents (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    content TEXT,
    source_url VARCHAR(1000) NOT NULL UNIQUE,
    document_type VARCHAR(100),
    category VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    vectorized BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
);

# 5. 创建向量存储表（Spring AI PGVector使用）
CREATE TABLE IF NOT EXISTS vector_store (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content TEXT NOT NULL,
    metadata JSONB,
    embedding vector(1536)  -- 对应通义千问的向量维度
);

# 6. 创建对话记忆表（Spring AI Chat Memory使用）
CREATE TABLE IF NOT EXISTS ai_chat_memory (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    conversation_id VARCHAR(255) NOT NULL,
    message_type VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

# 7. 创建索引以提高查询性能
CREATE INDEX IF NOT EXISTS idx_knowledge_documents_source_url ON knowledge_documents(source_url);
CREATE INDEX IF NOT EXISTS idx_knowledge_documents_type ON knowledge_documents(document_type);
CREATE INDEX IF NOT EXISTS idx_knowledge_documents_category ON knowledge_documents(category);
CREATE INDEX IF NOT EXISTS idx_knowledge_documents_status ON knowledge_documents(status);
CREATE INDEX IF NOT EXISTS idx_knowledge_documents_vectorized ON knowledge_documents(vectorized);
CREATE INDEX IF NOT EXISTS idx_knowledge_documents_created_at ON knowledge_documents(created_at);

# 向量相似度搜索索引
CREATE INDEX IF NOT EXISTS idx_vector_store_embedding ON vector_store 
USING hnsw (embedding vector_cosine_ops) WITH (m = 16, ef_construction = 64);

# 对话记忆索引
CREATE INDEX IF NOT EXISTS idx_chat_memory_conversation_id ON ai_chat_memory(conversation_id);
CREATE INDEX IF NOT EXISTS idx_chat_memory_created_at ON ai_chat_memory(created_at);

# 8. 创建数据库用户（可选）
-- CREATE USER qlu_user WITH PASSWORD 'qlu_password';
-- GRANT ALL PRIVILEGES ON DATABASE qlu_chatbot TO qlu_user;
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO qlu_user;
-- GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO qlu_user;

# 9. 插入一些示例数据
INSERT INTO knowledge_documents (title, content, source_url, document_type, category, status, vectorized) VALUES
('齐鲁工业大学学生手册', '齐鲁工业大学是山东省重点建设的应用研究型大学，前身为山东轻工业学院...', 'https://www.qlu.edu.cn/handbook', '规章制度', '学生处', 'COMPLETED', FALSE),
('本科生选课管理办法', '为规范本科生选课管理，保证教学秩序，特制定本办法...', 'https://www.qlu.edu.cn/course-selection', '规章制度', '教务处', 'COMPLETED', FALSE),
('2024年春季学期校历', '2024年春季学期开学时间为2月26日，期末考试时间为6月17日-21日...', 'https://www.qlu.edu.cn/calendar-2024', '通知公告', '教务处', 'COMPLETED', FALSE);

# 10. 验证数据库设置
SELECT 'Database setup completed successfully!' as status;
SELECT COUNT(*) as knowledge_documents_count FROM knowledge_documents;
SELECT version() as postgresql_version;
SELECT extname as installed_extensions FROM pg_extension WHERE extname = 'vector';

# 11. 显示表结构
\d knowledge_documents
\d vector_store
\d ai_chat_memory
