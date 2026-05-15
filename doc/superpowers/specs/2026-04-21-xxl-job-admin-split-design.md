# XXL-JOB Admin 模块拆分设计

## 1. 背景与目标

### 1.1 背景
当前 `xxl-job-admin` 模块包含：
- 调度核心逻辑（调度器、触发器、路由、告警等）
- Web 层（Controller、页面、SSO 登录、鉴权）
- 数据访问层（Mapper）
- 服务层（Service）

所有代码混在一起，不利于代码复用和职责分离。

### 1.2 目标
将 `xxl-job-admin` 拆分为两个独立模块：
- **xxl-job-admin-core**：调度核心，不包含任何 SSO、登录、鉴权、国际化、Response 等 Web 层概念
- **xxl-job-admin-web**：Web 层，包含 Controller、页面、SSO、登录、鉴权、国际化等

---

## 2. 拆分原则

| 原则 | 说明 |
|------|------|
| 核心独立性 | core 不依赖任何 Web 层类型（SSO、LoginInfo、Response、I18n 等） |
| 接口简单返回 | core 服务返回纯 Java 类型（boolean、int、List、Map 等） |
| 依赖单向 | web -> core，core 不依赖 web |
| 可独立发布 | core 可作为独立 artifact 发布 |

---

## 3. 模块设计

### 3.1 模块结构

```
xxl-job (parent pom)
├── xxl-job-core              (已有)
├── xxl-job-admin-core        (新增)
│   └── groupId: com.xuxueli
│   └── artifactId: xxl-job-admin-core
│   └── 依赖: xxl-job-core, mybatis, spring-context, spring-jdbc
│
└── xxl-job-admin-web         (原 xxl-job-admin 重命名)
    └── groupId: com.xuxueli
    └── artifactId: xxl-job-admin-web
    └── 依赖: xxl-job-core, xxl-job-admin-core, spring-boot-web, xxl-sso, freemarker
```

### 3.2 core 模块包结构

```
com.xxl.job.admin.core
├── constant/                 # 常量（TriggerStatus 等）
├── model/                     # 实体模型（XxlJobInfo, XxlJobLog 等）
├── mapper/                    # MyBatis Mapper 接口
├── scheduler/                 # 调度核心
│   ├── alarm/                 # 告警
│   ├── complete/              # 完成处理
│   ├── config/                # 启动配置
│   ├── cron/                  # Cron 表达式
│   ├── exception/             # 异常
│   ├── misfire/               # 错过触发处理
│   ├── route/                 # 路由策略
│   ├── thread/                # 调度线程（JobScheduleHelper 等）
│   ├── trigger/               # 触发器
│   └── type/                  # 调度类型
├── service/                   # 核心服务（按功能拆分）
│   ├── JobInfoService.java          # 任务 CRUD、启动/停止/触发
│   ├── JobGroupService.java         # 执行器分组
│   ├── JobLogService.java           # 日志查询
│   ├── JobCodeService.java          # GLUE 代码
│   ├── JobUserService.java          # 用户管理（不含 SSO）
│   └── DashboardService.java        # 监控数据
└── util/                      # 纯工具类（不含 Web 类型）
```

### 3.3 web 模块包结构

```
com.xxl.job.admin.web
├── controller/
│   ├── base/                  # 登录、首页等基础 Controller
│   └── biz/                  # 业务 Controller
│       ├── JobInfoController.java     # 鉴权后调用 core service
│       ├── JobGroupController.java
│       ├── JobLogController.java
│       ├── JobCodeController.java
│       └── JobUserController.java
├── util/                      # Web 层工具（含 SSO 依赖）
│   ├── I18nUtil.java
│   └── JobGroupPermissionUtil.java
├── web/                       # SSO、错误处理等
│   ├── xxlsso/
│   └── error/
├── XxlJobAdminApplication.java
└── resources/
    ├── templates/             # FreeMarker 模板
    ├── static/                # 静态资源
    └── i18n/                  # 国际化资源
```

### 3.4 架构特点

| 层级 | core 模块 | web 模块 |
|------|----------|---------|
| Controller | ❌ 无 | ✅ 负责 HTTP 请求处理、鉴权、视图返回 |
| Service | ✅ 核心业务逻辑（不含 Web 类型） | ❌ 无（Controller 直接调用 core） |
| Scheduler | ✅ 调度引擎 | ❌ 无 |
| Mapper | ✅ 数据访问 | ❌ 无 |
| View | ❌ 无 | ✅ 页面模板、静态资源 |

**核心原则：**
- Controller 做鉴权后，直接调用 core 的 `XxlJobService`
- core 服务返回纯 Java 类型（int、boolean、List、Map）
- web 层自行处理响应包装（Response）和国际化（I18n）

---

## 4. 服务接口设计

### 4.1 Core 服务接口（按功能拆分）

#### JobInfoService - 任务管理
```java
public interface JobInfoService {
    int add(XxlJobInfo jobInfo, int userId);                          // 新增任务
    boolean update(XxlJobInfo jobInfo, int userId);                   // 更新任务
    boolean remove(int id, int userId);                              // 删除任务
    boolean start(int id, int userId);                               // 启动调度
    boolean stop(int id, int userId);                                // 停止调度
    boolean trigger(int jobId, int userId, String executorParam, String addressList);  // 手动触发
    PageModel<XxlJobInfo> pageList(int offset, int pagesize, int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author);  // 分页查询
    List<String> generateNextTriggerTime(XxlJobInfo jobInfo);        // 计算下次触发时间
}
```

#### JobGroupService - 执行器分组
```java
public interface JobGroupService {
    List<XxlJobGroup> findAll();                                      // 查询所有分组
    XxlJobGroup load(int id);                                        // 加载单个分组
    int add(XxlJobGroup jobGroup, int userId);                        // 新增分组
    boolean update(XxlJobGroup jobGroup, int userId);                 // 更新分组
    boolean remove(int id, int userId);                              // 删除分组
    PageModel<XxlJobGroup> pageList(int offset, int pagesize, String searchName);  // 分页查询
}
```

#### JobLogService - 日志管理
```java
public interface JobLogService {
    PageModel<XxlJobLog> pageList(int offset, int pagesize, int jobGroup, int jobId, int logStatus, String startTime, String endTime);  // 分页查询日志
    XxlJobLog load(int id);                                          // 加载日志详情
    Map<String, Object> getLogStatGraph(int jobId, String fromTime, String toTime);  // 日志统计图
    boolean kill(int id, int userId);                                // 终止任务
}
```

#### JobCodeService - GLUE 代码
```java
public interface JobCodeService {
    String loadCode(int jobId);                                      // 加载 GLUE 代码
    boolean updateCode(int jobId, String glueSource, int userId);    // 更新 GLUE 代码
    List<XxlJobLogGlue> findJobLogGlues(int jobId);                 // 查询 GLUE 记录
}
```

#### JobUserService - 用户管理（不含 SSO 登录）
```java
public interface JobUserService {
    XxlJobUser loadByUserName(String userName);                     // 根据用户名查询
    int add(XxlJobUser jobUser, int userId);                         // 新增用户
    boolean update(XxlJobUser jobUser, int userId);                  // 更新用户
    boolean remove(int id, int userId);                             // 删除用户
    PageModel<XxlJobUser> pageList(int offset, int pagesize, String searchName);  // 分页查询
    boolean updatePassword(int userId, String oldPassword, String password);  // 修改密码
}
```

#### DashboardService - 监控面板
```java
public interface DashboardService {
    Map<String, Object> getDashboardInfo();                         // 仪表盘信息
    Map<String, Object> getChartInfo(Date startDate, Date endDate); // 图表数据
}
```

### 4.2 Controller 职责（web 模块）

Controller 职责：
1. 从 request 中提取 LoginInfo（SSO）
2. 权限校验（JobGroupPermissionUtil）
3. 调用 core 的 `XxlJobService`
4. 包装 Response 返回

**示例 - JobInfoController.add():**
```java
@PostMapping("/insert")
@ResponseBody
public Response<String> add(HttpServletRequest request, XxlJobInfo jobInfo) {
    // 1. 鉴权
    LoginInfo loginInfo = JobGroupPermissionUtil.validJobGroupPermission(request, jobInfo.getJobGroup());
    int userId = Integer.parseInt(loginInfo.getUserId());

    // 2. 调用 core 服务
    int newId = xxlJobService.add(jobInfo, userId);

    // 3. 包装响应
    if (newId > 0) {
        return Response.ofSuccess(String.valueOf(newId));
    } else {
        return Response.ofFail(I18nUtil.getString("jobinfo_field_add") + I18nUtil.getString("system_fail"));
    }
}
```

---

## 5. 文件迁移清单

### 5.1 迁入 core 的文件（约 60+ 文件）

| 目录 | 文件 | 说明 |
|------|------|------|
| `constant/` | Consts.java, TriggerStatus.java | 常量 |
| `model/` | XxlJobInfo.java, XxlJobLog.java 等 | 实体（不含 LoginInfo） |
| `mapper/` | XxlJobInfoMapper.java 等 | MyBatis Mapper 接口 |
| `scheduler/` | 全部子包 | 调度核心 |
| `service/` | 按功能拆分的 service 接口和实现 | |
| `util/` | 纯工具类（不含 Web 类型） | |

### 5.2 保留在 web 的文件

| 目录 | 文件 | 说明 |
|------|------|------|
| `controller/` | LoginController, IndexController, biz/* | Controller（鉴权后调用 core） |
| `util/` | I18nUtil.java, JobGroupPermissionUtil.java | Web 工具（含 SSO 依赖） |
| `web/` | xxlsso/*, error/* | SSO、错误处理 |
| `templates/` | 全部 ftl | 页面模板 |
| `static/` | 全部 | 静态资源 |
| `i18n/` | 全部 | 国际化资源 |

---

## 6. 依赖调整

### 6.1 core pom.xml 依赖

```xml
<dependencies>
    <!-- xxl-job-core -->
    <dependency>
        <groupId>com.xuxueli</groupId>
        <artifactId>xxl-job-core</artifactId>
    </dependency>

    <!-- spring -->
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-jdbc</artifactId>
    </dependency>

    <!-- mybatis -->
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
    </dependency>

    <!-- tools -->
    <dependency>
        <groupId>com.xuxueli</groupId>
        <artifactId>xxl-tool</artifactId>
    </dependency>
</dependencies>
```

### 6.2 web pom.xml 依赖

```xml
<dependencies>
    <!-- 依赖 core -->
    <dependency>
        <groupId>com.xuxueli</groupId>
        <artifactId>xxl-job-admin-core</artifactId>
    </dependency>

    <!-- spring boot web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- xxl-sso -->
    <dependency>
        <groupId>com.xuxueli</groupId>
        <artifactId>xxl-sso-core</artifactId>
    </dependency>

    <!-- freemarker, mail, actuator -->
    ...
</dependencies>
```

---

## 7. 实施步骤

### Phase 1: 创建 core 模块骨架
1. 创建 `xxl-job-admin-core` 目录结构
2. 创建 `pom.xml`
3. 迁移 `constant/` 包
4. 迁移 `model/` 包
5. 迁移 `mapper/` 包

### Phase 2: 迁移 scheduler
6. 迁移 `scheduler/alarm/`
7. 迁移 `scheduler/complete/`
8. 迁移 `scheduler/config/`
9. 迁移 `scheduler/cron/`
10. 迁移 `scheduler/exception/`
11. 迁移 `scheduler/misfire/`
12. 迁移 `scheduler/openapi/`
13. 迁移 `scheduler/route/`
14. 迁移 `scheduler/thread/`
15. 迁移 `scheduler/trigger/`
16. 迁移 `scheduler/type/`

### Phase 3: 迁移并重构 service 层
17. 创建 `JobInfoService` 接口和实现（从原 XxlJobService 拆分）
18. 创建 `JobGroupService` 接口和实现
19. 创建 `JobLogService` 接口和实现
20. 创建 `JobCodeService` 接口和实现
21. 创建 `JobUserService` 接口和实现
22. 创建 `DashboardService` 接口和实现
23. 迁移 `util/` 中纯工具类
24. 保留 `AdminBizImpl`（RPC 回调接口）

### Phase 4: 创建 web 模块
20. 将原 `xxl-job-admin` 重命名为 `xxl-job-admin-web`
21. 删除 service/ 目录（core 已包含）
22. 修改 Controller：
    - `JobInfoController` 调用 `JobInfoService`
    - `JobGroupController` 调用 `JobGroupService`
    - `JobLogController` 调用 `JobLogService`
    - `JobCodeController` 调用 `JobCodeService`
    - `JobUserController` 调用 `JobUserService`
    - `IndexController` 调用 `DashboardService`
23. 迁移 Web 层工具（I18nUtil, JobGroupPermissionUtil）

### Phase 5: 配置与测试
24. 创建 `xxl-job-admin-core` pom.xml
25. 调整父 pom.xml 模块列表
26. 调整 application.properties
27. 验证编译和运行

---

## 8. 风险与注意事项

1. **循环依赖风险**：确保 core 不引入任何 Web 类型
2. **接口兼容性**：拆分后 API 行为需保持一致
3. **数据库事务**：跨模块事务需要特别注意
4. **配置分离**：core 和 web 可能有不同的配置需求

---

## 9. 附录：文件清单

### 迁入 core 的 Java 文件（约 60 个）

```
constant/Consts.java
constant/TriggerStatus.java
model/XxlJobGroup.java
model/XxlJobInfo.java
model/XxlJobLog.java
model/XxlJobLogGlue.java
model/XxlJobLogReport.java
model/XxlJobRegistry.java
model/XxlJobUser.java
model/dto/XxlBootResourceDTO.java
mapper/XxlJobGroupMapper.java
mapper/XxlJobInfoMapper.java
mapper/XxlJobLockMapper.java
mapper/XxlJobLogGlueMapper.java
mapper/XxlJobLogMapper.java
mapper/XxlJobLogReportMapper.java
mapper/XxlJobRegistryMapper.java
mapper/XxlJobUserMapper.java
scheduler/alarm/JobAlarm.java
scheduler/alarm/JobAlarmer.java
scheduler/alarm/impl/EmailJobAlarm.java
scheduler/complete/JobCompleter.java
scheduler/config/XxlJobAdminBootstrap.java
scheduler/cron/CronExpression.java
scheduler/exception/XxlJobException.java
scheduler/misfire/MisfireHandler.java
scheduler/misfire/MisfireStrategyEnum.java
scheduler/misfire/strategy/MisfireDoNothing.java
scheduler/misfire/strategy/MisfireFireOnceNow.java
scheduler/openapi/OpenApiController.java
scheduler/route/ExecutorRouter.java
scheduler/route/ExecutorRouteStrategyEnum.java
scheduler/route/strategy/*.java (9 files)
scheduler/thread/*.java (6 files)
scheduler/trigger/JobTrigger.java
scheduler/trigger/TriggerTypeEnum.java
scheduler/type/ScheduleTypeEnum.java
scheduler/type/strategy/*.java
service/JobInfoService.java
service/JobGroupService.java
service/JobLogService.java
service/JobCodeService.java
service/JobUserService.java
service/DashboardService.java
service/impl/JobInfoServiceImpl.java
service/impl/JobGroupServiceImpl.java
service/impl/JobLogServiceImpl.java
service/impl/JobCodeServiceImpl.java
service/impl/JobUserServiceImpl.java
service/impl/DashboardServiceImpl.java
service/impl/AdminBizImpl.java
util/ (待评估)
```

### 保留在 web 的 Java 文件

```
controller/base/IndexController.java
controller/base/LoginController.java
controller/biz/JobInfoController.java
controller/biz/JobGroupController.java
controller/biz/JobLogController.java
controller/biz/JobCodeController.java
controller/biz/JobUserController.java
util/I18nUtil.java
util/JobGroupPermissionUtil.java
web/xxlsso/*
web/error/*
XxlJobAdminApplication.java
```
