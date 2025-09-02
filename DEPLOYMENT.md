# 齐鲁工业大学智能教务机器人 - 部署指南

## 项目概述

本项目是基于 **Spring AI Alibaba** 框架构建的智能化教务机器人，为齐鲁工业大学的师生提供智能化的教务咨询服务。

### 核心功能特性

- **🤖 智能问答**: 基于通义千问大模型的自然语言理解与生成
- **📚 RAG知识库**: 从学校官网自动采集并构建知识库
- **💬 对话记忆**: 支持多轮对话的上下文记忆
- **🔧 函数调用**: 与实时教务系统交互查询动态数据
- **⏰ 定时更新**: 自动定时采集最新的学校信息

### 技术栈架构

```
Frontend: HTML5 + JavaScript (简单示例)
    ↓
Backend: Spring Boot 3.4.9 + Spring AI Alibaba 1.0.0.2
    ↓
AI Service: 阿里巴巴通义千问 (via DashScope API)
    ↓
Database: PostgreSQL 16.10 + pgvector 0.8.0
    ↓
Data Sources: 齐鲁工业大学官网群 (qlu.edu.cn)
```

## 环境准备

### 1. 系统要求

- **操作系统**: Windows 10/11, macOS, Linux
- **Java**: OpenJDK 17 (LTS) 或更高版本
- **Maven**: 3.6+ 
- **PostgreSQL**: 16.10+ (需支持pgvector扩展)
- **网络**: 需要访问阿里云DashScope API

### 2. 依赖服务安装

#### PostgreSQL + pgvector 安装

**Windows (使用PostgreSQL官方安装包):**
```powershell
# 1. 下载并安装PostgreSQL 16.10
# https://www.postgresql.org/download/windows/

# 2. 安装pgvector扩展
# 下载预编译的pgvector扩展或从源码编译
# https://github.com/pgvector/pgvector
```

**macOS (使用Homebrew):**
```bash
# 安装PostgreSQL
brew install postgresql@16

# 安装pgvector
brew install pgvector

# 启动PostgreSQL服务
brew services start postgresql@16
```

**Linux (Ubuntu/Debian):**
```bash
# 安装PostgreSQL
sudo apt update
sudo apt install postgresql-16 postgresql-contrib-16

# 安装pgvector扩展
sudo apt install postgresql-16-pgvector

# 启动服务
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

#### Java 17 安装
```bash
# Windows (使用Chocolatey)
choco install openjdk17

# macOS (使用Homebrew)
brew install openjdk@17

# Linux (Ubuntu/Debian)
sudo apt install openjdk-17-jdk
```

## 配置部署

### 1. 克隆项目
```bash
git clone <repository-url>
cd AI-example
```

### 2. 数据库初始化
```bash
# 连接到PostgreSQL
psql -U postgres

# 执行初始化脚本
\i database/init.sql
```

### 3. 配置阿里云DashScope API

#### 获取API密钥
1. 访问 [阿里云DashScope控制台](https://dashscope.console.aliyun.com/)
2. 创建应用并获取API Key
3. 确保账户有足够的调用额度

#### 配置环境变量
**Windows PowerShell:**
```powershell
# 设置环境变量
$env:DASHSCOPE_API_KEY="your-actual-dashscope-api-key"
$env:DB_USERNAME="qlu_user"
$env:DB_PASSWORD="qlu_password"

# 验证设置
echo $env:DASHSCOPE_API_KEY
```

**macOS/Linux Bash:**
```bash
# 设置环境变量
export DASHSCOPE_API_KEY="your-actual-dashscope-api-key"
export DB_USERNAME="qlu_user"  
export DB_PASSWORD="qlu_password"

# 添加到 ~/.bashrc 或 ~/.zshrc 以持久化
echo 'export DASHSCOPE_API_KEY="your-actual-dashscope-api-key"' >> ~/.bashrc
```

### 4. 修改配置文件

编辑 `src/main/resources/application.properties`:

```properties
# 数据库连接配置 (根据实际情况修改)
spring.datasource.url=jdbc:postgresql://localhost:5432/qlu_chatbot
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:your_password}

# DashScope API配置
spring.ai.alibaba.dashscope.api-key=${DASHSCOPE_API_KEY}

# 数据采集配置 (可根据需要调整)
app.data-collection.base-urls=https://www.qlu.edu.cn,https://teacher.qlu.edu.cn
app.data-collection.schedule=0 0 2 * * ?
```

## 构建与运行

### 1. 编译项目
```bash
# 使用Maven编译
mvn clean compile

# 运行测试 (可选)
mvn test

# 打包应用
mvn clean package -DskipTests
```

### 2. 启动应用
```bash
# 方式1: 使用Maven插件启动
mvn spring-boot:run

# 方式2: 运行打包的JAR文件
java -jar target/academic-affairs-chatbot-1.0.0.jar

# 方式3: 在IDE中直接运行AcademicAffairsChatbotApplication.main()
```

### 3. 验证部署

#### 检查应用状态
```bash
# 访问健康检查接口
curl http://localhost:8080/api/v1/health

# 预期响应:
# {
#   "status": "UP",
#   "service": "QLU Academic Affairs Chatbot",
#   "version": "1.0.0",
#   "timestamp": 1677654321000
# }
```

#### 访问Web界面
打开浏览器访问: http://localhost:8080

## API接口说明

### 核心聊天接口

#### POST /api/v1/chat
发送聊天消息
```json
请求体:
{
  "message": "齐鲁工业大学的选课流程是什么？",
  "conversationId": "conv_12345",
  "stream": false
}

响应:
{
  "message": "齐鲁工业大学的选课流程如下...",
  "conversationId": "conv_12345", 
  "timestamp": "2024-01-15T10:30:00",
  "status": "SUCCESS"
}
```

#### GET /api/v1/conversation/{conversationId}/history
获取对话历史

#### DELETE /api/v1/conversation/{conversationId}
清除对话历史

#### POST /api/v1/admin/collect-data
手动触发数据采集 (管理员接口)

## 数据采集配置

### 自动采集机制

应用会按照配置的Cron表达式自动从指定网站采集数据:

```properties
# 每天凌晨2点执行数据采集
app.data-collection.schedule=0 0 2 * * ?

# 采集目标网站
app.data-collection.base-urls=https://www.qlu.edu.cn,https://teacher.qlu.edu.cn

# 网络超时设置 (30秒)
app.data-collection.timeout=30000

# 失败重试次数
app.data-collection.retry-count=3
```

### 手动触发采集

```bash
# 使用curl触发数据采集
curl -X POST http://localhost:8080/api/v1/admin/collect-data

# 或在应用日志中查看定时任务执行情况
tail -f logs/application.log | grep "DataCollection"
```

## 生产环境部署

### 1. 容器化部署 (Docker)

创建 `Dockerfile`:
```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app
COPY target/academic-affairs-chatbot-1.0.0.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

构建和运行:
```bash
# 构建镜像
docker build -t qlu-chatbot:1.0.0 .

# 运行容器
docker run -d \
  --name qlu-chatbot \
  -p 8080:8080 \
  -e DASHSCOPE_API_KEY="your-api-key" \
  -e DB_USERNAME="qlu_user" \
  -e DB_PASSWORD="qlu_password" \
  qlu-chatbot:1.0.0
```

### 2. 云服务部署

#### 阿里云ECS部署
1. 创建ECS实例 (推荐配置: 2核4GB, CentOS 7+)
2. 安装Java 17和PostgreSQL
3. 配置安全组开放8080端口
4. 上传JAR包并配置系统服务

#### 使用阿里云RDS
```properties
# 使用阿里云RDS PostgreSQL
spring.datasource.url=jdbc:postgresql://your-rds-endpoint:5432/qlu_chatbot
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

### 3. 监控与日志

#### 应用监控
```properties
# 启用Spring Boot Actuator
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=when-authorized
```

#### 日志配置
```properties
# 日志级别配置
logging.level.edu.qlu.chatbot=INFO
logging.level.org.springframework.ai=DEBUG
logging.level.com.alibaba.cloud.ai=DEBUG

# 日志文件配置
logging.file.name=logs/qlu-chatbot.log
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
```

## 故障排除

### 常见问题

#### 1. DashScope API调用失败
```bash
# 检查API密钥是否正确设置
echo $DASHSCOPE_API_KEY

# 检查网络连接
curl -H "Authorization: Bearer $DASHSCOPE_API_KEY" \
     https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation

# 检查账户余额和调用限额
```

#### 2. 数据库连接问题
```bash
# 测试数据库连接
psql -h localhost -p 5432 -U qlu_user -d qlu_chatbot

# 检查pgvector扩展是否安装
psql -d qlu_chatbot -c "SELECT extname FROM pg_extension WHERE extname = 'vector';"

# 检查表是否正确创建
psql -d qlu_chatbot -c "\dt"
```

#### 3. 网页抓取失败
```bash
# 检查目标网站是否可访问
curl -I https://www.qlu.edu.cn

# 查看抓取日志
grep "DataCollection" logs/qlu-chatbot.log

# 手动触发数据采集测试
curl -X POST http://localhost:8080/api/v1/admin/collect-data
```

#### 4. 向量检索不工作
```sql
-- 检查向量存储表
SELECT COUNT(*) FROM vector_store;

-- 检查向量维度设置
\d vector_store

-- 重建向量索引
DROP INDEX IF EXISTS idx_vector_store_embedding;
CREATE INDEX idx_vector_store_embedding ON vector_store 
USING hnsw (embedding vector_cosine_ops) WITH (m = 16, ef_construction = 64);
```

### 性能优化建议

1. **数据库优化**:
   - 适当调整PostgreSQL配置参数
   - 定期执行VACUUM和ANALYZE
   - 监控查询性能

2. **应用优化**:
   - 调整JVM堆内存设置
   - 配置连接池参数
   - 启用应用监控

3. **网络优化**:
   - 使用CDN加速静态资源
   - 配置负载均衡
   - 启用GZIP压缩

## 后续扩展

### 功能扩展方向

1. **多模态支持**: 支持图片和文档上传
2. **语音交互**: 集成语音识别和合成
3. **个性化推荐**: 基于用户行为的智能推荐
4. **移动端适配**: 开发微信小程序或移动App
5. **多语言支持**: 支持英文等多语言交互

### 技术演进

1. **微服务化**: 拆分为多个微服务
2. **容器编排**: 使用Kubernetes进行容器编排
3. **服务网格**: 引入Istio等服务网格技术
4. **AI能力增强**: 集成更多AI模型和服务

---

## 联系支持

如有技术问题或建议，请联系开发团队:
- 邮箱: support@qlu.edu.cn
- 技术文档: [项目Wiki](https://github.com/your-repo/wiki)
- 问题反馈: [GitHub Issues](https://github.com/your-repo/issues)

---

**注意**: 本项目为齐鲁工业大学内部使用，请确保遵守相关的数据安全和隐私保护规定。
