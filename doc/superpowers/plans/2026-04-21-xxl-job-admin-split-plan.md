# XXL-JOB Admin 模块拆分实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将 xxl-job-admin 拆分为 xxl-job-admin-core（调度核心）和 xxl-job-admin-web（Web 层）

**Architecture:** 平行模块结构，web 依赖 core，core 不依赖 web。core 包含调度引擎、Service（不含 Web 类型）、Mapper；web 包含 Controller、页面、SSO、鉴权。

**Tech Stack:** Java 17, Maven, Spring Boot, MyBatis, xxl-sso, FreeMarker

---

## 文件结构概览

### Phase 1: 创建 core 模块骨架

**Files:**
- Create: `xxl-job-admin-core/pom.xml`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/constant/Consts.java`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/constant/TriggerStatus.java`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/model/XxlJobGroup.java`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/model/XxlJobInfo.java`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/model/XxlJobLog.java`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/model/XxlJobLogGlue.java`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/model/XxlJobLogReport.java`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/model/XxlJobRegistry.java`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/model/XxlJobUser.java`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/model/dto/XxlBootResourceDTO.java`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/mapper/`
- Modify: `pom.xml` (父 pom 添加模块)

### Phase 2: 迁移 scheduler

**Files:**
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/scheduler/alarm/`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/scheduler/complete/`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/scheduler/config/`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/scheduler/cron/`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/scheduler/exception/`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/scheduler/misfire/`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/scheduler/openapi/`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/scheduler/route/`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/scheduler/thread/`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/scheduler/trigger/`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/scheduler/type/`

### Phase 3: 迁移并重构 service 层

**Files:**
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/service/JobInfoService.java`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/service/JobGroupService.java`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/service/JobLogService.java`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/service/JobCodeService.java`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/service/JobUserService.java`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/service/DashboardService.java`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/service/impl/JobInfoServiceImpl.java`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/service/impl/JobGroupServiceImpl.java`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/service/impl/JobLogServiceImpl.java`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/service/impl/JobCodeServiceImpl.java`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/service/impl/JobUserServiceImpl.java`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/service/impl/DashboardServiceImpl.java`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/service/impl/AdminBizImpl.java`
- Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/util/` (待迁移的纯工具类)

### Phase 4: 创建 web 模块

**Files:**
- Create: `xxl-job-admin-web/` (原 xxl-job-admin 目录)
- Modify: `xxl-job-admin-web/controller/biz/JobInfoController.java`
- Modify: `xxl-job-admin-web/controller/biz/JobGroupController.java`
- Modify: `xxl-job-admin-web/controller/biz/JobLogController.java`
- Modify: `xxl-job-admin-web/controller/biz/JobCodeController.java`
- Modify: `xxl-job-admin-web/controller/biz/JobUserController.java`
- Modify: `xxl-job-admin-web/controller/base/IndexController.java`
- Delete: `xxl-job-admin-web/service/` (迁移到 core)
- Modify: `xxl-job-admin-web/pom.xml` (添加 core 依赖)

### Phase 5: 配置与测试

**Files:**
- Modify: `pom.xml` (父 pom 模块列表)
- Modify: `xxl-job-admin-web/src/main/resources/application.properties`
- Delete: 原 `xxl-job-admin/` 目录内容（迁移完成后）

---

## 详细任务列表

### Phase 1: 创建 core 模块骨架

#### Task 1: 创建 xxl-job-admin-core 目录结构和 pom.xml

- [ ] **Step 1: 创建 core 模块目录**

```bash
mkdir -p xxl-job-admin-core/src/main/java/com/xxl/job/admin/core
mkdir -p xxl-job-admin-core/src/main/resources
mkdir -p xxl-job-admin-core/src/test/java/com/xxl/job/admin/core
```

- [ ] **Step 2: 创建 core pom.xml**

Create: `xxl-job-admin-core/pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.xuxueli</groupId>
        <artifactId>xxl-job</artifactId>
        <version>3.4.1-SNAPSHOT</version>
    </parent>
    <artifactId>xxl-job-admin-core</artifactId>
    <packaging>jar</packaging>

    <properties>
        <maven.deploy.skip>true</maven.deploy.skip>
    </properties>

    <dependencies>
        <!-- xxl-job-core -->
        <dependency>
            <groupId>com.xuxueli</groupId>
            <artifactId>xxl-job-core</artifactId>
        </dependency>

        <!-- spring-context -->
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

        <!-- xxl-tool -->
        <dependency>
            <groupId>com.xuxueli</groupId>
            <artifactId>xxl-tool</artifactId>
        </dependency>

        <!-- slf4j -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 3: 提交**

```bash
git add xxl-job-admin-core/
git commit -m "feat: create xxl-job-admin-core module skeleton"
```

#### Task 2: 迁移 constant 包

- [ ] **Step 1: 复制 constant 文件到 core**

从 `xxl-job-admin/src/main/java/com/xxl/job/admin/constant/` 复制到 `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/constant/`

需要复制的文件：
- `Consts.java`
- `TriggerStatus.java`

- [ ] **Step 2: 修改 package 声明**

将 `package com.xxl.job.admin.constant;` 改为 `package com.xxl.job.admin.core.constant;`

- [ ] **Step 3: 从原位置删除**

```bash
git rm xxl-job-admin/src/main/java/com/xxl/job/admin/constant/
```

- [ ] **Step 4: 提交**

```bash
git commit -m "refactor: move constant package to core module"
```

#### Task 3: 迁移 model 包

- [ ] **Step 1: 复制 model 文件到 core**

从 `xxl-job-admin/src/main/java/com/xxl/job/admin/model/` 复制到 `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/model/`

需要复制的文件：
- `XxlJobGroup.java`
- `XxlJobInfo.java`
- `XxlJobLog.java`
- `XxlJobLogGlue.java`
- `XxlJobLogReport.java`
- `XxlJobRegistry.java`
- `XxlJobUser.java`
- `dto/XxlBootResourceDTO.java`

- [ ] **Step 2: 修改 package 声明**

将 `package com.xxl.job.admin.model;` 改为 `package com.xxl.job.admin.core.model;`
将 `package com.xxl.job.admin.model.dto;` 改为 `package com.xxl.job.admin.core.model.dto;`

- [ ] **Step 3: 提交**

```bash
git commit -m "refactor: move model package to core module"
```

#### Task 4: 迁移 mapper 包

- [ ] **Step 1: 复制 mapper 文件到 core**

从 `xxl-job-admin/src/main/java/com/xxl/job/admin/mapper/` 复制到 `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/mapper/`

需要复制的文件：
- `XxlJobGroupMapper.java`
- `XxlJobInfoMapper.java`
- `XxlJobLockMapper.java`
- `XxlJobLogGlueMapper.java`
- `XxlJobLogMapper.java`
- `XxlJobLogReportMapper.java`
- `XxlJobRegistryMapper.java`
- `XxlJobUserMapper.java`

- [ ] **Step 2: 修改 package 声明**

将 `package com.xxl.job.admin.mapper;` 改为 `package com.xxl.job.admin.core.mapper;`

- [ ] **Step 3: 复制 mapper XML 文件**

从 `xxl-job-admin/src/main/resources/mapper/` 复制到 `xxl-job-admin-core/src/main/resources/mapper/`

- [ ] **Step 4: 提交**

```bash
git commit -m "refactor: move mapper package to core module"
```

---

### Phase 2: 迁移 scheduler

#### Task 5: 迁移 scheduler 全部子包

- [ ] **Step 1: 复制 scheduler 目录到 core**

从 `xxl-job-admin/src/main/java/com/xxl/job/admin/scheduler/` 复制到 `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/scheduler/`

需要复制的子包：
- `alarm/` (含 impl/)
- `complete/`
- `config/`
- `cron/`
- `exception/`
- `misfire/` (含 strategy/)
- `openapi/`
- `route/` (含 strategy/)
- `thread/`
- `trigger/`
- `type/` (含 strategy/)

- [ ] **Step 2: 修改所有文件的 package 声明**

将 `package com.xxl.job.admin.scheduler.` 改为 `package com.xxl.job.admin.core.scheduler.`

- [ ] **Step 3: 更新所有 import 语句**

将 `com.xxl.job.admin.scheduler` 的 import 改为 `com.xxl.job.admin.core.scheduler`
将 `com.xxl.job.admin.constant` 的 import 改为 `com.xxl.job.admin.core.constant`
将 `com.xxl.job.admin.model` 的 import 改为 `com.xxl.job.admin.core.model`
将 `com.xxl.job.admin.mapper` 的 import 改为 `com.xxl.job.admin.core.mapper`

- [ ] **Step 4: 复制 scheduler 相关配置**

如果 `application.properties` 中有 scheduler 相关配置，需要拆分

- [ ] **Step 5: 提交**

```bash
git commit -m "refactor: move scheduler package to core module"
```

---

### Phase 3: 迁移并重构 service 层

#### Task 6: 创建 JobInfoService 接口和实现

- [ ] **Step 1: 创建 JobInfoService 接口**

Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/service/JobInfoService.java`

```java
package com.xxl.job.admin.core.service;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.tool.response.PageModel;

import java.util.List;

public interface JobInfoService {
    int add(XxlJobInfo jobInfo, int userId);
    boolean update(XxlJobInfo jobInfo, int userId);
    boolean remove(int id, int userId);
    boolean start(int id, int userId);
    boolean stop(int id, int userId);
    boolean trigger(int jobId, int userId, String executorParam, String addressList);
    PageModel<XxlJobInfo> pageList(int offset, int pagesize, int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author);
    List<String> generateNextTriggerTime(XxlJobInfo jobInfo);
}
```

- [ ] **Step 2: 创建 JobInfoServiceImpl 实现**

Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/service/impl/JobInfoServiceImpl.java`

从原 `XxlJobServiceImpl` 中提取 JobInfo 相关逻辑，重构为：
- 移除 `LoginInfo` 参数，改为 `int userId`
- 移除 `Response` 返回类型，改为原始类型
- 移除 `I18nUtil` 调用
- 移除 `JobGroupPermissionUtil` 调用（权限校验在 Web 层做）
- 保留 Cron 校验、调度时间计算等核心逻辑

- [ ] **Step 3: 提交**

```bash
git add xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/service/
git commit -m "feat(core): add JobInfoService interface and implementation"
```

#### Task 7: 创建 JobGroupService 接口和实现

- [ ] **Step 1: 创建 JobGroupService 接口**

Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/service/JobGroupService.java`

```java
package com.xxl.job.admin.core.service;

import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.tool.response.PageModel;

import java.util.List;

public interface JobGroupService {
    List<XxlJobGroup> findAll();
    XxlJobGroup load(int id);
    int add(XxlJobGroup jobGroup, int userId);
    boolean update(XxlJobGroup jobGroup, int userId);
    boolean remove(int id, int userId);
    PageModel<XxlJobGroup> pageList(int offset, int pagesize, String searchName);
}
```

- [ ] **Step 2: 创建 JobGroupServiceImpl 实现**

Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/service/impl/JobGroupServiceImpl.java`

从原 `JobGroupController` 中提取业务逻辑

- [ ] **Step 3: 提交**

```bash
git commit -m "feat(core): add JobGroupService interface and implementation"
```

#### Task 8: 创建 JobLogService 接口和实现

- [ ] **Step 1: 创建 JobLogService 接口**

Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/service/JobLogService.java`

```java
package com.xxl.job.admin.core.service;

import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.tool.response.PageModel;

import java.util.Map;

public interface JobLogService {
    PageModel<XxlJobLog> pageList(int offset, int pagesize, int jobGroup, int jobId, int logStatus, String startTime, String endTime);
    XxlJobLog load(int id);
    Map<String, Object> getLogStatGraph(int jobId, String fromTime, String toTime);
    boolean kill(int id, int userId);
}
```

- [ ] **Step 2: 创建 JobLogServiceImpl 实现**

Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/service/impl/JobLogServiceImpl.java`

- [ ] **Step 3: 提交**

```bash
git commit -m "feat(core): add JobLogService interface and implementation"
```

#### Task 9: 创建 JobCodeService 接口和实现

- [ ] **Step 1: 创建 JobCodeService 接口**

Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/service/JobCodeService.java`

```java
package com.xxl.job.admin.core.service;

import com.xxl.job.admin.core.model.XxlJobLogGlue;

import java.util.List;

public interface JobCodeService {
    String loadCode(int jobId);
    boolean updateCode(int jobId, String glueSource, int userId);
    List<XxlJobLogGlue> findJobLogGlues(int jobId);
}
```

- [ ] **Step 2: 创建 JobCodeServiceImpl 实现**

Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/service/impl/JobCodeServiceImpl.java`

- [ ] **Step 3: 提交**

```bash
git commit -m "feat(core): add JobCodeService interface and implementation"
```

#### Task 10: 创建 JobUserService 接口和实现

- [ ] **Step 1: 创建 JobUserService 接口**

Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/service/JobUserService.java`

```java
package com.xxl.job.admin.core.service;

import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.tool.response.PageModel;

public interface JobUserService {
    XxlJobUser loadByUserName(String userName);
    int add(XxlJobUser jobUser, int userId);
    boolean update(XxlJobUser jobUser, int userId);
    boolean remove(int id, int userId);
    PageModel<XxlJobUser> pageList(int offset, int pagesize, String searchName);
    boolean updatePassword(int userId, String oldPassword, String password);
}
```

- [ ] **Step 2: 创建 JobUserServiceImpl 实现**

Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/service/impl/JobUserServiceImpl.java`

注意：密码修改逻辑在 core 中，但密码校验结果返回给 web 层处理

- [ ] **Step 3: 提交**

```bash
git commit -m "feat(core): add JobUserService interface and implementation"
```

#### Task 11: 创建 DashboardService 接口和实现

- [ ] **Step 1: 创建 DashboardService 接口**

Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/service/DashboardService.java`

```java
package com.xxl.job.admin.core.service;

import java.util.Date;
import java.util.Map;

public interface DashboardService {
    Map<String, Object> getDashboardInfo();
    Map<String, Object> getChartInfo(Date startDate, Date endDate);
}
```

- [ ] **Step 2: 创建 DashboardServiceImpl 实现**

Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/service/impl/DashboardServiceImpl.java`

- [ ] **Step 3: 提交**

```bash
git commit -m "feat(core): add DashboardService interface and implementation"
```

#### Task 12: 迁移 AdminBizImpl

- [ ] **Step 1: 复制 AdminBizImpl 到 core**

AdminBizImpl 是 Executor 回调接口，需要保留在 core

Create: `xxl-job-admin-core/src/main/java/com/xxl/job/admin/core/service/impl/AdminBizImpl.java`

- [ ] **Step 2: 修改 package 声明**

将 `package com.xxl.job.admin.service.impl;` 改为 `package com.xxl.job.admin.core.service.impl;`

- [ ] **Step 3: 提交**

```bash
git commit -m "refactor: move AdminBizImpl to core service impl"
```

---

### Phase 4: 创建 web 模块

#### Task 13: 重命名 xxl-job-admin 为 xxl-job-admin-web

- [ ] **Step 1: 重命名目录**

```bash
mv xxl-job-admin xxl-job-admin-web
```

- [ ] **Step 2: 提交**

```bash
git commit -m "refactor: rename xxl-job-admin to xxl-job-admin-web"
```

#### Task 14: 修改 web 模块 pom.xml

- [ ] **Step 1: 修改 pom.xml**

Modify: `xxl-job-admin-web/pom.xml`

在 `<dependencies>` 中添加：

```xml
<!-- 依赖 core -->
<dependency>
    <groupId>com.xuxueli</groupId>
    <artifactId>xxl-job-admin-core</artifactId>
    <version>${project.parent.version}</version>
</dependency>
```

- [ ] **Step 2: 提交**

```bash
git commit -m "feat(web): add core module dependency"
```

#### Task 15: 删除 web 中的 service 目录

- [ ] **Step 1: 删除 service 目录**

```bash
rm -rf xxl-job-admin-web/src/main/java/com/xxl/job/admin/service/
```

- [ ] **Step 2: 提交**

```bash
git commit -m "refactor(web): remove service layer (moved to core)"
```

#### Task 16: 修改 JobInfoController

- [ ] **Step 1: 修改 JobInfoController**

Modify: `xxl-job-admin-web/src/main/java/com/xxl/job/admin/controller/biz/JobInfoController.java`

改动要点：
1. 将 `@Resource private XxlJobService xxlJobService;` 改为各 core service 的注入
2. 移除方法中的 SSO、LoginInfo 处理逻辑（保留鉴权）
3. 调用 core service 后自行包装 Response
4. 保留 I18nUtil 调用

示例改动：
```java
@Resource
private JobInfoService jobInfoService;  // 原来是 XxlJobService

@RequestMapping("/insert")
@ResponseBody
public Response<String> add(HttpServletRequest request, XxlJobInfo jobInfo) {
    // 鉴权（保留）
    LoginInfo loginInfo = JobGroupPermissionUtil.validJobGroupPermission(request, jobInfo.getJobGroup());
    int userId = Integer.parseInt(loginInfo.getUserId());

    // 调用 core 服务
    int newId = jobInfoService.add(jobInfo, userId);

    // 包装响应
    if (newId > 0) {
        return Response.ofSuccess(String.valueOf(newId));
    } else {
        return Response.ofFail(I18nUtil.getString("jobinfo_field_add") + I18nUtil.getString("system_fail"));
    }
}
```

- [ ] **Step 2: 提交**

```bash
git commit -m "refactor(web): update JobInfoController to use core service"
```

#### Task 17: 修改其他 Controller

- [ ] **Step 1: 修改 JobGroupController**

Modify: `xxl-job-admin-web/src/main/java/com/xxl/job/admin/controller/biz/JobGroupController.java`

- [ ] **Step 2: 修改 JobLogController**

Modify: `xxl-job-admin-web/src/main/java/com/xxl/job/admin/controller/biz/JobLogController.java`

- [ ] **Step 3: 修改 JobCodeController**

Modify: `xxl-job-admin-web/src/main/java/com/xxl/job/admin/controller/biz/JobCodeController.java`

- [ ] **Step 4: 修改 JobUserController**

Modify: `xxl-job-admin-web/src/main/java/com/xxl/job/admin/controller/biz/JobUserController.java`

- [ ] **Step 5: 修改 IndexController**

Modify: `xxl-job-admin-web/src/main/java/com/xxl/job/admin/controller/base/IndexController.java`

注入 DashboardService 用于获取监控数据

- [ ] **Step 6: 提交**

```bash
git commit -m "refactor(web): update all controllers to use core services"
```

---

### Phase 5: 配置与测试

#### Task 18: 修改父 pom.xml

- [ ] **Step 1: 修改父 pom.xml 模块列表**

Modify: `pom.xml`

将：
```xml
<modules>
    <module>xxl-job-core</module>
    <module>xxl-job-admin</module>
    <module>xxl-job-executor-samples</module>
</modules>
```

改为：
```xml
<modules>
    <module>xxl-job-core</module>
    <module>xxl-job-admin-core</module>
    <module>xxl-job-admin-web</module>
    <module>xxl-job-executor-samples</module>
</modules>
```

- [ ] **Step 2: 提交**

```bash
git commit -m "refactor: update parent pom modules list"
```

#### Task 19: 调整 application.properties

- [ ] **Step 1: 评估配置拆分**

检查 `xxl-job-admin-web/src/main/resources/application.properties`：
- 数据库配置保留在 web（或移至 core）
- 调度器相关配置移至 core
- SSO 配置保留在 web

- [ ] **Step 2: 如需要，创建 core 配置**

可能需要在 `xxl-job-admin-core/src/main/resources/` 创建配置文件

- [ ] **Step 3: 提交**

```bash
git commit -m "refactor: adjust application properties for module split"
```

#### Task 20: 验证编译

- [ ] **Step 1: 编译项目**

```bash
cd d:/Projects/Github/xxl-job
mvn clean compile -DskipTests
```

- [ ] **Step 2: 如有编译错误，修复并提交**

预期可能的问题：
- import 语句未完全更新
- 循环依赖（core 引用了 web 类型）
- 配置缺失

- [ ] **Step 3: 最终提交**

```bash
git commit -m "fix: resolve compilation issues after module split"
```

---

## 实施检查清单

完成所有任务后，验证以下内容：

- [ ] `xxl-job-admin-core` 模块可独立编译
- [ ] `xxl-job-admin-web` 模块依赖 `xxl-job-admin-core`
- [ ] 所有 Controller 调用 core service，不直接操作 DB
- [ ] core 不包含任何 `com.xxl.sso` 或 `com.xxl.tool.response.Response` 的引用
- [ ] I18nUtil、JobGroupPermissionUtil 保留在 web 层
- [ ] 原有功能保持一致

---

## 风险与注意事项

1. **循环依赖检查**：确保 core 的 import 中没有 `com.xxl.job.admin.web` 或 `com.xxl.job.admin.controller`
2. **事务边界**：如果 service 方法需要事务，确保 `@Transactional` 在 core 层
3. **配置分离**：有些配置可能需要同时存在于两个模块
4. **RPC 回调**：AdminBizImpl 需要在 core 中，确保调度线程能访问到

**Plan saved to:** `docs/superpowers/plans/2026-04-21-xxl-job-admin-split-plan.md`
