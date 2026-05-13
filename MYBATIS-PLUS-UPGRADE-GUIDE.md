# MyBatis-Plus 升级方案 (注解驱动)

> 从 MyBatis 升级到 MyBatis-Plus 最佳实践指南
> **方案特点**: 全部删除 XML，使用注解 (@Select, @Insert, @Update, @Delete) 替代

---

## 1. 当前项目现状分析

### 1.1 技术栈

| 项目 | 当前版本 |
|------|----------|
| Spring Boot | 3.5.10 |
| MyBatis | 3.x |
| MySQL | 8.x |

### 1.2 现有 Mapper 结构

```
mapper/
├── XxlJobGroupMapper.java + XxlJobGroupMapper.xml
├── XxlJobInfoMapper.java + XxlJobInfoMapper.xml
├── XxlJobLockMapper.java + XxlJobLockMapper.xml
├── XxlJobLogGlueMapper.java + XxlJobLogGlueMapper.xml
├── XxlJobLogMapper.java + XxlJobLogMapper.xml
├── XxlJobLogReportMapper.java + XxlJobLogReportMapper.xml
├── XxlJobRegistryMapper.java + XxlJobRegistryMapper.xml
└── XxlJobUserMapper.java + XxlJobUserMapper.xml
```

**共 8 个 Mapper 接口 + 8 个 XML 文件**

---

## 2. MyBatis-Plus 注解方案优势

### 2.1 注解 vs XML 对比

| 特性 | XML | 注解 |
|------|-----|------|
| SQL 可读性 | 较好 | 更好 |
| 动态 SQL | 灵活 | @Script + Provider |
| 复杂查询 | 适合 | 一般 |
| 简单 CRUD | 冗余 | 简洁 |
| 代码位置 | 分散 | 集中 |
| MyBatis-Plus 支持 | ✅ | ✅ |

### 2.2 注解方式优势

| 优势 | 说明 |
|------|------|
| **代码集中** | SQL 就在 Mapper 接口中，一眼可见 |
| **类型安全** | 编译期检查 |
| **IDE 支持** | 代码补全、跳转 |
| **无 XML 解析** | 启动更快 |
| **版本控制** | 合并冲突更简单 |

---

## 3. 升级步骤

### 3.1 步骤总览

```
┌─────────────────────────────────────────────────────────────┐
│  1. 添加依赖                                              │
│  2. 配置 MyBatis-Plus                                      │
│  3. 改造 Entity 实体类 (Lombok + MP 注解)                  │
│  4. 改造 Mapper 接口 (注解 + 继承 BaseMapper)              │
│  5. 删除全部 XML 文件                                       │
│  6. 测试验证                                               │
└─────────────────────────────────────────────────────────────┘
```

### 3.2 第一步：添加 Maven 依赖

**pom.xml**:
```xml
<!-- 删除原有 MyBatis 依赖 -->
<!--
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>3.x.x</version>
</dependency>
-->

<!-- 添加 MyBatis-Plus 依赖 -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.7</version>
</dependency>
```

### 3.3 第二步：配置 MyBatis-Plus

**application.properties**:
```properties
# 新增 MyBatis-Plus 配置
mybatis-plus:
  # 配置实体扫描路径
  type-aliases-package: com.xxl.job.admin.model
  # 配置全局策略
  global-config:
    db-config:
      # 主键类型: AUTO 自增
      id-type: AUTO
      # 逻辑删除字段 (可选)
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
  # 配置日志 (开发时开启)
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    # 开启下划线转驼峰
    map-underscore-to-camel-case: true
```

### 3.4 第三步：改造 Entity 实体类

**XxlJobInfo 改造示例**:

```java
package com.xxl.job.admin.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("xxl_job_info")
public class XxlJobInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID - 自增
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 执行器主键ID
     */
    private Integer jobGroup;

    /**
     * 任务描述
     */
    private String jobDesc;

    /**
     * 创建时间 - 自动填充
     */
    @TableField(fill = FieldFill.INSERT)
    private Date addTime;

    /**
     * 更新时间 - 自动填充
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 负责人
     */
    private String author;

    /**
     * 报警邮件
     */
    private String alarmEmail;

    /**
     * 调度类型
     */
    private String scheduleType;

    /**
     * 调度配置
     */
    private String scheduleConf;

    /**
     * 调度过期策略
     */
    private String misfireStrategy;

    /**
     * 执行器路由策略
     */
    private String executorRouteStrategy;

    /**
     * 执行器Handler名称
     */
    private String executorHandler;

    /**
     * 执行器参数
     */
    private String executorParam;

    /**
     * 阻塞处理策略
     */
    private String executorBlockStrategy;

    /**
     * 任务执行超时时间，单位秒
     */
    private Integer executorTimeout;

    /**
     * 失败重试次数
     */
    private Integer executorFailRetryCount;

    /**
     * GLUE类型
     */
    private String glueType;

    /**
     * GLUE源代码
     */
    private String glueSource;

    /**
     * GLUE备注
     */
    private String glueRemark;

    /**
     * GLUE更新时间
     */
    private Date glueUpdatetime;

    /**
     * 子任务ID，多个逗号分隔
     */
    private String childJobId;

    /**
     * 调度状态
     */
    private Integer triggerStatus;

    /**
     * 上次调度时间
     */
    private Long triggerLastTime;

    /**
     * 下次调度时间
     */
    private Long triggerNextTime;
}
```

### 3.5 第四步：改造 Mapper 接口 (全注解)

**XxlJobInfoMapper 改造示例**:

```java
package com.xxl.job.admin.mapper;

import com.baomidou.mybatisplus.core.annotation.SqlParser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxl.job.admin.model.XxlJobInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 任务信息 Mapper
 * 继承 BaseMapper<Entity> 后，自动获得基础 CRUD 方法
 * 自定义复杂查询使用 @Select/@Insert/@Update/@Delete 注解
 */
@Mapper
public interface XxlJobInfoMapper extends BaseMapper<XxlJobInfo> {

    // ==================== 基础 CRUD 已继承 BaseMapper ====================
    // baseMapper.insert(entity)          → 插入
    // baseMapper.deleteById(id)         → 删除
    // baseMapper.updateById(entity)     → 更新
    // baseMapper.selectById(id)        → 查询
    // baseMapper.selectList(wrapper)    → 列表查询
    // baseMapper.selectPage(page, w)   → 分页查询

    // ==================== 自定义复杂查询 ====================

    /**
     * 分页查询任务列表
     *
     * 注解说明:
     * - @Select: 查询注解
     * - @Param: 参数绑定
     * - 复杂条件使用 ${varName} 拼接 (防注入用 #{} )
     */
    @Select("<script>" +
            "SELECT * FROM xxl_job_info AS t" +
            "<where>" +
            "   <if test='jobGroup != null and jobGroup > 0'>" +
            "       AND t.job_group = #{jobGroup}" +
            "   </if>" +
            "   <if test='triggerStatus != null and triggerStatus >= 0'>" +
            "       AND t.trigger_status = #{triggerStatus}" +
            "   </if>" +
            "   <if test='jobDesc != null and jobDesc != \"\"'>" +
            "       AND t.job_desc LIKE CONCAT('%', #{jobDesc}, '%')" +
            "   </if>" +
            "   <if test='executorHandler != null and executorHandler != \"\"'>" +
            "       AND t.executor_handler LIKE CONCAT('%', #{executorHandler}, '%')" +
            "   </if>" +
            "   <if test='author != null and author != \"\"'>" +
            "       AND t.author LIKE CONCAT('%', #{author}, '%')" +
            "   </if>" +
            "</where>" +
            " ORDER BY t.id DESC" +
            " LIMIT #{offset}, #{pagesize}" +
            "</script>")
    List<XxlJobInfo> pageList(@Param("offset") int offset,
                               @Param("pagesize") int pagesize,
                               @Param("jobGroup") Integer jobGroup,
                               @Param("triggerStatus") Integer triggerStatus,
                               @Param("jobDesc") String jobDesc,
                               @Param("executorHandler") String executorHandler,
                               @Param("author") String author);

    /**
     * 查询任务列表总数
     */
    @Select("<script>" +
            "SELECT COUNT(1) FROM xxl_job_info AS t" +
            "<where>" +
            "   <if test='jobGroup != null and jobGroup > 0'>" +
            "       AND t.job_group = #{jobGroup}" +
            "   </if>" +
            "   <if test='triggerStatus != null and triggerStatus >= 0'>" +
            "       AND t.trigger_status = #{triggerStatus}" +
            "   </if>" +
            "   <if test='jobDesc != null and jobDesc != \"\"'>" +
            "       AND t.job_desc LIKE CONCAT('%', #{jobDesc}, '%')" +
            "   </if>" +
            "   <if test='executorHandler != null and executorHandler != \"\"'>" +
            "       AND t.executor_handler LIKE CONCAT('%', #{executorHandler}, '%')" +
            "   </if>" +
            "   <if test='author != null and author != \"\"'>" +
            "       AND t.author LIKE CONCAT('%', #{author}, '%')" +
            "   </if>" +
            "</where>" +
            "</script>")
    int pageListCount(@Param("offset") int offset,
                      @Param("pagesize") int pagesize,
                      @Param("jobGroup") Integer jobGroup,
                      @Param("triggerStatus") Integer triggerStatus,
                      @Param("jobDesc") String jobDesc,
                      @Param("executorHandler") String executorHandler,
                      @Param("author") String author);

    /**
     * 根据执行器组查询任务列表
     */
    @Select("SELECT * FROM xxl_job_info AS t WHERE t.job_group = #{jobGroup}")
    List<XxlJobInfo> getJobsByGroup(@Param("jobGroup") int jobGroup);

    /**
     * 查询任务总数
     */
    @Select("SELECT COUNT(1) FROM xxl_job_info")
    int findAllCount();

    /**
     * 调度任务查询 - 查询需要触发的任务
     */
    @Select("SELECT * FROM xxl_job_info AS t " +
            "WHERE t.trigger_status = 1 " +
            "AND t.trigger_next_time &lt;= #{maxNextTime} " +
            "ORDER BY t.id ASC " +
            "LIMIT #{pagesize}")
    List<XxlJobInfo> scheduleJobQuery(@Param("maxNextTime") long maxNextTime,
                                      @Param("pagesize") int pagesize);

    /**
     * 更新调度状态
     * 只能更新 trigger_status = 1 的任务，避免停用任务被启用
     */
    @Update("UPDATE xxl_job_info SET " +
            "trigger_last_time = #{triggerLastTime}, " +
            "trigger_next_time = #{triggerNextTime}" +
            " <if test='triggerStatus != null and triggerStatus >= 0'>" +
            ", trigger_status = #{triggerStatus}" +
            " </if>" +
            " WHERE id = #{id} AND trigger_status = 1")
    int scheduleUpdate(XxlJobInfo xxlJobInfo);
}
```

### 3.6 第五步：删除全部 XML 文件

```
删除以下文件:
├── resources/mapper/XxlJobGroupMapper.xml
├── resources/mapper/XxlJobInfoMapper.xml
├── resources/mapper/XxlJobLockMapper.xml
├── resources/mapper/XxlJobLogGlueMapper.xml
├── resources/mapper/XxlJobLogMapper.xml
├── resources/mapper/XxlJobLogReportMapper.xml
├── resources/mapper/XxlJobRegistryMapper.xml
└── resources/mapper/XxlJobUserMapper.xml
```

### 3.7 第六步：添加自动填充处理器

```java
package com.xxl.job.admin.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Supplier;

/**
 * MyBatis-Plus 自动填充处理器
 */
@Component
public class MyBatisPlusMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // 插入时填充创建时间和更新时间
        this.strictInsertFill(metaObject, "addTime", Date::new, Date.class);
        this.strictInsertFill(metaObject, "updateTime", Date::new, Date.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新时填充更新时间
        this.strictUpdateFill(metaObject, "updateTime", Date::new, Date.class);
    }
}
```

---

## 4. 各 Mapper 改造详情

### 4.1 XxlJobInfoMapper

```java
@Mapper
public interface XxlJobInfoMapper extends BaseMapper<XxlJobInfo> {

    // 继承方法 (BaseMapper):
    // - insert(), deleteById(), updateById(), selectById()

    // 自定义方法 (注解):
    List<XxlJobInfo> pageList(...);
    int pageListCount(...);
    List<XxlJobInfo> getJobsByGroup(int jobGroup);
    int findAllCount();
    List<XxlJobInfo> scheduleJobQuery(...);
    int scheduleUpdate(XxlJobInfo info);
}
```

### 4.2 XxlJobLogMapper

```java
@Mapper
public interface XxlJobLogMapper extends BaseMapper<XxlJobLog> {

    // 分页查询
    @Select("<script>" +
            "SELECT * FROM xxl_job_log AS t" +
            "<where>" +
            "   <if test='jobGroup != null'>AND t.job_group = #{jobGroup}</if>" +
            "   <if test='jobId != null'>AND t.job_id = #{jobId}</if>" +
            "   <if test='logStatus != null'>AND t.log_status = #{logStatus}</if>" +
            "   <if test='author != null and author != \"\"'>AND t.author LIKE CONCAT('%', #{author}, '%')</if>" +
            "   <if test='optTimeStart != null and optTimeEnd != null'>" +
            "       AND t.trigger_time BETWEEN #{optTimeStart} AND #{optTimeEnd}" +
            "   </if>" +
            "</where>" +
            " ORDER BY t.id DESC LIMIT #{offset}, #{pagesize}" +
            "</script>")
    List<XxlJobLog> pageList(...);

    // 日志详情
    @Select("SELECT * FROM xxl_job_log WHERE id = #{id}")
    XxlJobLog loadById(long id);

    // 按触发时间查询
    @Select("<script>" +
            "SELECT * FROM xxl_job_log" +
            " WHERE trigger_time &lt;= #{endTime}" +
            " AND trigger_time &gt;= #{startTime}" +
            "</script>")
    List<XxlJobLog> getLogsByTriggerTime(@Param("startTime") long startTime,
                                          @Param("endTime") long endTime);
}
```

### 4.3 XxlJobGroupMapper

```java
@Mapper
public interface XxlJobGroupMapper extends BaseMapper<XxlJobGroup> {

    // 查询所有执行器组
    @Select("SELECT * FROM xxl_job_group ORDER BY id ASC")
    List<XxlJobGroup> findAll();

    // 根据 AppName 查询
    @Select("SELECT * FROM xxl_job_group WHERE appname = #{appName}")
    XxlJobGroup loadByAppName(@Param("appName") String appName);
}
```

### 4.4 XxlJobRegistryMapper

```java
@Mapper
public interface XxlJobRegistryMapper extends BaseMapper<XxlJobRegistry> {

    // 清理超时注册 (超时时间 < 当前时间)
    @Delete("DELETE FROM xxl_job_registry " +
            "WHERE registry_key = #{key} " +
            "AND registry_value = #{value} " +
            "AND update_time &lt; #{timeout}")
    int removeDead(@Param("key") String key,
                   @Param("value") String value,
                   @Param("timeout") long timeout);

    // 查询所有在线执行器
    @Select("SELECT * FROM xxl_job_registry " +
            "WHERE registry_key = #{appName} " +
            "AND registry_group = 'EXECUTOR' " +
            "AND update_time &gt; #{timeout}")
    List<XxlJobRegistry> findAllByAppNameAndTimeout(@Param("appName") String appName,
                                                     @Param("timeout") long timeout);
}
```

### 4.5 XxlJobLockMapper

```java
@Mapper
public interface XxlJobLockMapper extends BaseMapper<XxlJobLock> {

    // 分布式锁查询
    @Select("SELECT * FROM xxl_job_lock WHERE lock_name = #{lockName}")
    XxlJobLock selectByLockName(@Param("lockName") String lockName);
}
```

### 4.6 XxlJobUserMapper

```java
@Mapper
public interface XxlJobUserMapper extends BaseMapper<XxlJobUser> {

    // 根据用户名查询
    @Select("SELECT * FROM xxl_job_user WHERE username = #{username}")
    XxlJobUser loadByUsername(@Param("username") String username);

    // 登录验证
    @Select("SELECT * FROM xxl_job_user WHERE username = #{username} AND password = #{password}")
    XxlJobUser login(@Param("username") String username, @Param("password") String password);
}
```

---

## 5. 注解 SQL 最佳实践

### 5.1 动态 SQL 语法

```java
@Select("<script>" +
        "SELECT * FROM table_name" +
        "<where>" +
        "   <if test='param != null'>AND column = #{param}</if>" +
        "   <if test='strParam != null and strParam != \"\"'>" +
        "       AND name LIKE CONCAT('%', #{strParam}, '%')" +
        "   </if>" +
        "</where>" +
        " ORDER BY id DESC" +
        "</script>")
List<Entity> selectList(...);
```

### 5.2 常用标签

| 标签 | 用途 |
|------|------|
| `<if test="...">` | 条件判断 |
| `<where>` | 智能 WHERE (去除多余 AND/OR) |
| `<set>` | 智能 SET (去除多余逗号) |
| `<trim prefix="..." suffix="...">` | 自定义trim |
| `<foreach collection="...">` | 循环遍历 |
| `<choose><when test="..."></when></choose>` | switch-case |
| `<bind name="..." value="...">` | 定义变量 |

### 5.3 特殊字符转义

| 符号 | XML 中写法 | 说明 |
|------|-----------|------|
| < | `&lt;` 或 `<` | 小于 |
| > | `&gt;` 或 `>` | 大于 |
| <= | `&lt;=` | 小于等于 |
| >= | `&gt;=` | 大于等于 |

---

## 6. LambdaQueryWrapper 使用 (替代注解方式)

如果 Mapper 方法特别复杂，可以在 Service 层使用 LambdaQueryWrapper：

```java
@Service
public class XxlJobInfoServiceImpl extends ServiceImpl<XxlJobInfoMapper, XxlJobInfo>
    implements XxlJobInfoService {

    @Override
    public IPage<XxlJobInfo> pageList(int offset, int pagesize, Integer jobGroup,
                                       Integer triggerStatus, String jobDesc,
                                       String executorHandler, String author) {

        // 构建分页
        int pageNum = (offset / pagesize) + 1;
        Page<XxlJobInfo> page = new Page<>(pageNum, pagesize);

        // 构建条件 - 类型安全
        LambdaQueryWrapper<XxlJobInfo> wrapper = new LambdaQueryWrapper<>();

        if (jobGroup != null && jobGroup > 0) {
            wrapper.eq(XxlJobInfo::getJobGroup, jobGroup);
        }
        if (triggerStatus != null && triggerStatus >= 0) {
            wrapper.eq(XxlJobInfo::getTriggerStatus, triggerStatus);
        }
        if (StringUtils.isNotBlank(jobDesc)) {
            wrapper.like(XxlJobInfo::getJobDesc, jobDesc);
        }
        if (StringUtils.isNotBlank(executorHandler)) {
            wrapper.like(XxlJobInfo::getExecutorHandler, executorHandler);
        }
        if (StringUtils.isNotBlank(author)) {
            wrapper.like(XxlJobInfo::getAuthor, author);
        }

        wrapper.orderByDesc(XxlJobInfo::getId);

        return this.page(page, wrapper);
    }
}
```

**选择建议**:

| 场景 | 推荐方式 |
|------|----------|
| 简单 CRUD | BaseMapper 内置方法 |
| 中等复杂度 | 注解 @Select |
| 非常复杂 | LambdaQueryWrapper |
| 动态 SQL 极复杂 | 保留 XML (不推荐) |

---

## 7. 改造效果对比

### 7.1 文件数量对比

| 类型 | 改造前 | 改造后 |
|------|--------|--------|
| Java Entity | 8 个 (每个 200+ 行) | 8 个 (每个 60 行) |
| Java Mapper | 8 个 (每个 60+ 行) | 8 个 (每个 30 行) |
| XML 文件 | 8 个 (每个 200+ 行) | 0 个 |
| **代码行数** | **~5000 行** | **~1000 行** |
| **减少** | - | **80%** |

### 7.2 功能对比

| 能力 | 改造前 | 改造后 |
|------|--------|--------|
| 单表 CRUD | 手写 XML | 自动 (BaseMapper) |
| 分页查询 | 手写 XML + Plugin | 注解 / LambdaWrapper |
| 条件查询 | 手写 XML | 注解 / LambdaWrapper |
| 代码量 | 多 | 少 |
| 可维护性 | 差 | 好 |

---

## 8. 风险控制

### 8.1 兼容性风险

| 风险 | 解决方案 |
|------|----------|
| Spring Boot 3 兼容 | 使用 `mybatis-plus-spring-boot3-starter` |
| MySQL 8 兼容 | 确保使用 `mysql-connector-j` 8.x |
| 动态 SQL 限制 | 使用 `<script>` 标签包裹 |

### 8.2 测试计划

1. **单元测试**: 测试每个 Mapper 方法
2. **集成测试**: 测试 Service 层
3. **回归测试**: 确保原有功能不受影响
4. **性能测试**: 对比改造前后性能

---

## 9. 实施计划

### 9.1 第一阶段：基础设施 (0.5天)

- [ ] 添加 Maven 依赖
- [ ] 配置 MyBatis-Plus
- [ ] 添加自动填充处理器

### 9.2 第二阶段：Entity 改造 (1天)

- [ ] 改造 XxlJobInfo
- [ ] 改造 XxlJobLog
- [ ] 改造 XxlJobGroup
- [ ] 改造 XxlJobRegistry
- [ ] 改造其他 Entity

### 9.3 第三阶段：Mapper 改造 (1.5天)

- [ ] 改造 XxlJobInfoMapper (注解)
- [ ] 改造 XxlJobLogMapper (注解)
- [ ] 改造 XxlJobGroupMapper (注解)
- [ ] 改造 XxlJobRegistryMapper (注解)
- [ ] 改造其他 Mapper (注解)

### 9.4 第四阶段：删除 XML (0.5天)

- [ ] 删除 resources/mapper/ 下所有 XML

### 9.5 第五阶段：测试验证 (1天)

- [ ] 单元测试
- [ ] 集成测试
- [ ] 回归测试

**预计总工期**: **4.5 天**

---

## 10. 附录

### 10.1 MyBatis-Plus 注解一览

| 注解 | 作用 |
|------|------|
| @Select | 查询 |
| @Insert | 插入 |
| @Update | 更新 |
| @Delete | 删除 |
| @Param | 参数绑定 |
| @Options | 选项配置 |
| @Result | 结果映射 |
| @Results | 结果映射集 |

### 10.2 注解 SQL 示例

```java
// 简单查询
@Select("SELECT * FROM user WHERE id = #{id}")
User selectById(int id);

// 条件查询
@Select("SELECT * FROM user WHERE name = #{name} AND age >= #{age}")
List<User> selectByNameAndAge(String name, int age);

// 插入
@Insert("INSERT INTO user(name, age) VALUES(#{name}, #{age})")
int insert(User user);

// 更新
@Update("UPDATE user SET name = #{name} WHERE id = #{id}")
int update(User user);

// 删除
@Delete("DELETE FROM user WHERE id = #{id}")
int delete(int id);

// 动态 SQL
@Select("<script>SELECT * FROM user <where>...</where></script>")
List<User> selectDynamic(...);
```

### 10.3 完整示例: XxlJobInfoMapper.java

```java
package com.xxl.job.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxl.job.admin.model.XxlJobInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 任务信息 Mapper
 *
 * 使用说明:
 * 1. 继承 BaseMapper<Entity> 获得基础 CRUD
 * 2. 复杂查询使用 @Select/@Insert/@Update/@Delete 注解
 * 3. 动态 SQL 使用 <script> 标签包裹
 *
 * @author xuxueli
 */
@Mapper
public interface XxlJobInfoMapper extends BaseMapper<XxlJobInfo> {

    // ==================== 基础 CRUD 已继承 BaseMapper ====================
    // baseMapper.insert(entity)              → INSERT
    // baseMapper.deleteById(id)             → DELETE BY ID
    // baseMapper.deleteBatchIds(ids)        → DELETE BY IDS
    // baseMapper.updateById(entity)         → UPDATE BY ID
    // baseMapper.selectById(id)             → SELECT BY ID
    // baseMapper.selectBatchIds(ids)       → SELECT BY IDS
    // baseMapper.selectList(wrapper)       → SELECT BY WRAPPER
    // baseMapper.selectPage(page, wrapper)  → SELECT PAGE

    // ==================== 自定义复杂查询 ====================

    /**
     * 分页查询任务列表
     */
    @Select("<script>" +
            "SELECT * FROM xxl_job_info AS t" +
            "<where>" +
            "   <if test='jobGroup != null and jobGroup > 0'>" +
            "       AND t.job_group = #{jobGroup}" +
            "   </if>" +
            "   <if test='triggerStatus != null and triggerStatus >= 0'>" +
            "       AND t.trigger_status = #{triggerStatus}" +
            "   </if>" +
            "   <if test='jobDesc != null and jobDesc != \"\"'>" +
            "       AND t.job_desc LIKE CONCAT('%', #{jobDesc}, '%')" +
            "   </if>" +
            "   <if test='executorHandler != null and executorHandler != \"\"'>" +
            "       AND t.executor_handler LIKE CONCAT('%', #{executorHandler}, '%')" +
            "   </if>" +
            "   <if test='author != null and author != \"\"'>" +
            "       AND t.author LIKE CONCAT('%', #{author}, '%')" +
            "   </if>" +
            "</where>" +
            " ORDER BY t.id DESC" +
            " LIMIT #{offset}, #{pagesize}" +
            "</script>")
    List<XxlJobInfo> pageList(@Param("offset") int offset,
                               @Param("pagesize") int pagesize,
                               @Param("jobGroup") Integer jobGroup,
                               @Param("triggerStatus") Integer triggerStatus,
                               @Param("jobDesc") String jobDesc,
                               @Param("executorHandler") String executorHandler,
                               @Param("author") String author);

    /**
     * 查询任务列表总数
     */
    @Select("<script>" +
            "SELECT COUNT(1) FROM xxl_job_info AS t" +
            "<where>" +
            "   <if test='jobGroup != null and jobGroup > 0'>" +
            "       AND t.job_group = #{jobGroup}" +
            "   </if>" +
            "   <if test='triggerStatus != null and triggerStatus >= 0'>" +
            "       AND t.trigger_status = #{triggerStatus}" +
            "   </if>" +
            "   <if test='jobDesc != null and jobDesc != \"\"'>" +
            "       AND t.job_desc LIKE CONCAT('%', #{jobDesc}, '%')" +
            "   </if>" +
            "   <if test='executorHandler != null and executorHandler != \"\"'>" +
            "       AND t.executor_handler LIKE CONCAT('%', #{executorHandler}, '%')" +
            "   </if>" +
            "   <if test='author != null and author != \"\"'>" +
            "       AND t.author LIKE CONCAT('%', #{author}, '%')" +
            "   </if>" +
            "</where>" +
            "</script>")
    int pageListCount(@Param("offset") int offset,
                      @Param("pagesize") int pagesize,
                      @Param("jobGroup") Integer jobGroup,
                      @Param("triggerStatus") Integer triggerStatus,
                      @Param("jobDesc") String jobDesc,
                      @Param("executorHandler") String executorHandler,
                      @Param("author") String author);

    /**
     * 根据执行器组查询任务列表
     */
    @Select("SELECT * FROM xxl_job_info AS t WHERE t.job_group = #{jobGroup}")
    List<XxlJobInfo> getJobsByGroup(@Param("jobGroup") int jobGroup);

    /**
     * 查询任务总数
     */
    @Select("SELECT COUNT(1) FROM xxl_job_info")
    int findAllCount();

    /**
     * 调度任务查询 - 查询需要触发的任务
     */
    @Select("SELECT * FROM xxl_job_info AS t " +
            "WHERE t.trigger_status = 1 " +
            "AND t.trigger_next_time &lt;= #{maxNextTime} " +
            "ORDER BY t.id ASC " +
            "LIMIT #{pagesize}")
    List<XxlJobInfo> scheduleJobQuery(@Param("maxNextTime") long maxNextTime,
                                      @Param("pagesize") int pagesize);

    /**
     * 更新调度状态
     * 只能更新 trigger_status = 1 的任务，避免停用任务被启用
     */
    @Update("UPDATE xxl_job_info SET " +
            "trigger_last_time = #{triggerLastTime}, " +
            "trigger_next_time = #{triggerNextTime}" +
            " <if test='triggerStatus != null and triggerStatus >= 0'>" +
            ", trigger_status = #{triggerStatus}" +
            " </if>" +
            " WHERE id = #{id} AND trigger_status = 1")
    int scheduleUpdate(XxlJobInfo xxlJobInfo);
}
```
