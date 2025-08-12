# XXL-JOB PostgreSQL 配置说明

## 改动内容

已将 xxl-job-admin 从 MySQL 数据库迁移到 PostgreSQL 数据库，主要改动包括：

### 1. 依赖修改
- **文件**: `xxl-job-admin/pom.xml`
- **改动**: 将 MySQL 驱动替换为 PostgreSQL 驱动
```xml
<!-- 移除 mysql-connector-j -->
<!-- 添加 postgresql 驱动 -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.3</version>
</dependency>
```

### 2. 数据库连接配置
- **文件**: `xxl-job-admin/src/main/resources/application.properties`
- **改动**: 修改数据库连接信息
```properties
spring.datasource.url=jdbc:postgresql://127.0.0.1:5432/xxl_job?useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver
```

### 3. 数据库初始化脚本
- **新增文件**: `doc/db/tables_xxl_job_postgresql.sql`
- **改动**: 创建了兼容 PostgreSQL 的初始化脚本，主要变化：
  - 使用 SERIAL 代替 AUTO_INCREMENT
  - 使用 TIMESTAMP 代替 DATETIME
  - 使用 SMALLINT 代替 TINYINT
  - 使用 TEXT 代替 MEDIUMTEXT
  - 调整索引创建语法
  - 添加序列重置语句

### 4. MyBatis 映射文件
修改了所有 MyBatis XML 映射文件以兼容 PostgreSQL：
- 移除所有反引号 (`)
- 将 `CONCAT()` 函数改为 `||` 操作符
- 将 `LIMIT offset, count` 改为 `LIMIT count OFFSET offset`
- 将 `DATE_ADD()` 改为 PostgreSQL 的 INTERVAL 算术
- 将 `ON DUPLICATE KEY UPDATE` 改为 `ON CONFLICT ... DO UPDATE`

## 使用步骤

### 1. 安装 PostgreSQL
确保已安装 PostgreSQL 数据库（推荐版本 12 或更高）

### 2. 创建数据库
使用 PostgreSQL 客户端执行初始化脚本：
```bash
psql -U postgres -f doc/db/tables_xxl_job_postgresql.sql
```

### 3. 配置数据库连接
修改 `application.properties` 中的数据库连接信息：
- 数据库地址
- 用户名
- 密码

### 4. 编译项目
```bash
mvn clean package -DskipTests
```

### 5. 启动服务
```bash
cd xxl-job-admin
java -jar target/xxl-job-admin-*.jar
```

### 6. 访问管理界面
浏览器访问: http://localhost:8080/xxl-job-admin
- 默认用户名: admin
- 默认密码: 123456

## 注意事项

1. **数据迁移**: 如果需要从 MySQL 迁移现有数据，需要使用数据迁移工具
2. **性能调优**: PostgreSQL 的配置可能需要根据实际使用情况进行调优
3. **备份策略**: 请定期备份 PostgreSQL 数据库
4. **兼容性**: 所有 SQL 语句已调整为 PostgreSQL 兼容格式

## 测试验证

建议进行以下测试：
1. 创建执行器组
2. 创建定时任务
3. 手动执行任务
4. 查看任务日志
5. 用户登录和权限管理