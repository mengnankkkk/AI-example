-- 齐鲁工业大学智能教务机器人数据库初始化脚本
-- PostgreSQL + pgvector 扩展

-- 创建pgvector扩展（用于向量存储）
CREATE EXTENSION IF NOT EXISTS vector;

-- 创建知识文档表
DROP TABLE IF EXISTS knowledge_documents CASCADE;

CREATE TABLE knowledge_documents (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    content TEXT NOT NULL,
    source_url VARCHAR(1000) UNIQUE NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    category VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    vectorized BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) DEFAULT 'PENDING'
);

-- 创建向量存储表（Spring AI使用）
DROP TABLE IF EXISTS vector_store CASCADE;

CREATE TABLE vector_store (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    content TEXT NOT NULL,
    metadata JSONB,
    embedding vector(1536)  -- OpenAI/DashScope embedding维度
);

-- 创建索引以提高查询性能
CREATE INDEX idx_knowledge_documents_source_url ON knowledge_documents(source_url);
CREATE INDEX idx_knowledge_documents_document_type ON knowledge_documents(document_type);
CREATE INDEX idx_knowledge_documents_category ON knowledge_documents(category);
CREATE INDEX idx_knowledge_documents_status ON knowledge_documents(status);
CREATE INDEX idx_knowledge_documents_vectorized ON knowledge_documents(vectorized);
CREATE INDEX idx_knowledge_documents_updated_at ON knowledge_documents(updated_at);
CREATE INDEX idx_knowledge_documents_created_at ON knowledge_documents(created_at);

-- 为向量相似性搜索创建索引
CREATE INDEX idx_vector_store_embedding ON vector_store 
USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);

-- 创建全文搜索索引
CREATE INDEX idx_knowledge_documents_content_fts ON knowledge_documents 
USING gin(to_tsvector('chinese', title || ' ' || content));

-- 创建更新时间戳触发器函数
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 为knowledge_documents表创建更新时间戳触发器
CREATE TRIGGER update_knowledge_documents_updated_at 
    BEFORE UPDATE ON knowledge_documents 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 插入一些示例数据（可选）
INSERT INTO knowledge_documents (title, content, source_url, document_type, category, vectorized, status) VALUES
('学生成绩查询指南', '学生可以通过学生信息系统登录后查看个人成绩，包括期末考试成绩、平时成绩等。', 'https://www.qlu.edu.cn/jwc/grade-query', 'ACADEMIC_AFFAIRS', '教务处', false, 'COMPLETED'),
('选课系统使用说明', '选课系统开放时间为每学期第1-2周，学生需要在规定时间内完成课程选择。', 'https://www.qlu.edu.cn/jwc/course-selection', 'ACADEMIC_AFFAIRS', '教务处', false, 'COMPLETED'),
('学籍管理规定', '学生学籍管理包括入学注册、转专业、休学复学等相关规定和流程。', 'https://www.qlu.edu.cn/jwc/student-status', 'ACADEMIC_AFFAIRS', '教务处', false, 'COMPLETED'),
('奖学金申请流程', '各类奖学金的申请条件、申请时间、申请材料和评审流程说明。', 'https://www.qlu.edu.cn/xsc/scholarship', 'STUDENT_AFFAIRS', '学生处', false, 'COMPLETED'),
('宿舍管理规定', '学生宿舍入住、管理规定、安全须知等相关信息。', 'https://www.qlu.edu.cn/xsc/dormitory', 'STUDENT_AFFAIRS', '学生处', false, 'COMPLETED');

-- 创建数据统计视图
CREATE OR REPLACE VIEW knowledge_statistics AS
SELECT 
    document_type,
    category,
    status,
    COUNT(*) as doc_count,
    SUM(CASE WHEN vectorized THEN 1 ELSE 0 END) as vectorized_count,
    MAX(updated_at) as last_updated
FROM knowledge_documents 
GROUP BY document_type, category, status;

-- 创建最近文档视图
CREATE OR REPLACE VIEW recent_documents AS
SELECT 
    id,
    title,
    category,
    document_type,
    source_url,
    updated_at
FROM knowledge_documents 
WHERE updated_at >= CURRENT_DATE - INTERVAL '7 days'
ORDER BY updated_at DESC;

-- 权限设置（如果需要特定用户）
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO qlu_user;
-- GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO qlu_user;
