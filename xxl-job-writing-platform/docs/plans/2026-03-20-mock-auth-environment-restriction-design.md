# 模拟认证环境限制设计方案

## 概述
根据Codex安全复审报告建议，需要限制模拟认证（mock-enabled）仅在开发环境中启用，确保生产环境不启用模拟认证。本设计采用Spring Profiles实现环境配置分离。

## 设计决策
- **方案选择**: 基于Spring Profiles的环境配置分离（用户选择推荐方案）
- **核心目标**: 防止模拟认证在生产环境被意外启用
- **验证机制**: 应用启动时检查配置一致性

## 配置文件结构

### 1. 基础配置 (application.yml)
包含所有环境的公共设置：
```yaml
writing:
  platform:
    jwt:
      secret: ${JWT_SECRET:}
      expiration-hours: ${JWT_EXPIRATION_HOURS:24}
      strict-validation: ${JWT_STRICT_VALIDATION:true}

    # 认证配置（将在profile-specific文件中被覆盖）
    auth:
      mock-enabled: ${AUTH_MOCK_ENABLED:false}
      mock-credentials:
        admin:
          password: ${AUTH_MOCK_ADMIN_PASSWORD:}
          user-id: 1
          user-type: 0
        expert:
          password: ${AUTH_MOCK_EXPERT_PASSWORD:}
          user-id: 2
          user-type: 1
```

### 2. 开发环境配置 (application-dev.yml)
```yaml
# 开发环境 - 启用模拟认证用于测试
writing:
  platform:
    jwt:
      strict-validation: false  # 开发环境允许较短密钥

    auth:
      mock-enabled: true
      mock-credentials:
        admin:
          password: ${AUTH_MOCK_ADMIN_PASSWORD:admin123}
        expert:
          password: ${AUTH_MOCK_EXPERT_PASSWORD:expert123}

# 开发环境特定配置
springdoc:
  swagger-ui:
    enabled: true  # 开发环境启用Swagger
```

### 3. 生产环境配置 (application-prod.yml)
```yaml
# 生产环境 - 强制禁用模拟认证
writing:
  platform:
    jwt:
      strict-validation: true  # 生产环境强制严格验证

    auth:
      mock-enabled: false  # 明确禁用模拟认证

# 生产环境安全配置
server:
  ssl:
    enabled: true  # 启用HTTPS
springdoc:
  swagger-ui:
    enabled: false  # 生产环境禁用Swagger
```

### 4. 本地开发配置 (application-local.yml) - 可选
```yaml
# 本地开发环境 - 可自定义的测试配置
writing:
  platform:
    auth:
      mock-credentials:
        admin:
          password: ${AUTH_MOCK_ADMIN_PASSWORD:localadmin}
        expert:
          password: ${AUTH_MOCK_EXPERT_PASSWORD:localexpert}
```

## 配置验证增强

### AuthController配置验证
在`AuthController`中添加`@PostConstruct`方法验证配置：
```java
@PostConstruct
public void validateMockConfiguration() {
    Environment env = applicationContext.getEnvironment();
    boolean isProd = Arrays.asList(env.getActiveProfiles()).contains("prod");

    if (mockEnabled && isProd) {
        throw new IllegalStateException(
            "Mock authentication cannot be enabled in production environment. " +
            "Check application-prod.yml configuration."
        );
    }

    if (mockEnabled) {
        log.info("Mock authentication enabled for profiles: {}",
                 String.join(",", env.getActiveProfiles()));
    }
}
```

### JwtTokenUtil配置更新
更新现有的验证逻辑，考虑环境因素：
```java
@PostConstruct
public void validateConfiguration() {
    Environment env = applicationContext.getEnvironment();
    boolean isProd = Arrays.asList(env.getActiveProfiles()).contains("prod");
    boolean isDev = Arrays.asList(env.getActiveProfiles()).contains("dev");

    // JWT密钥验证
    if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
        throw new IllegalStateException("JWT secret must be configured");
    }

    // 生产环境强制严格验证
    if (isProd && !strictValidation) {
        throw new IllegalStateException(
            "Strict validation must be enabled in production environment"
        );
    }

    // 密钥长度警告（开发环境可宽松）
    if (jwtSecret.length() < 32) {
        if (strictValidation || isProd) {
            throw new IllegalStateException(
                String.format("JWT secret is too short (%d chars). " +
                            "Minimum required length is 32 characters.",
                            jwtSecret.length())
            );
        } else if (isDev) {
            log.warn("JWT secret is too short ({}) chars. " +
                    "Minimum recommended length is 32 characters.",
                    jwtSecret.length());
        }
    }
}
```

## 启动配置

### 环境变量控制
- 本地开发：`export SPRING_PROFILES_ACTIVE=dev,local`
- 生产部署：`export SPRING_PROFILES_ACTIVE=prod`
- 测试环境：`export SPRING_PROFILES_ACTIVE=test`

### 启动脚本示例
```bash
# 开发环境启动
java -jar writing-platform.jar --spring.profiles.active=dev,local

# 生产环境启动
java -jar writing-platform.jar --spring.profiles.active=prod
```

## 安全防护措施

1. **配置继承机制**: 生产环境配置显式覆盖并禁用模拟认证
2. **启动时验证**: 应用启动时检查配置一致性和环境安全性
3. **环境检测**: 根据Spring Profiles应用不同的安全策略
4. **日志记录**: 配置变更时记录清晰的日志信息
5. **环境隔离**: 不同环境的配置完全独立，减少误配置风险

## 实施步骤

1. 创建三个配置文件：`application-dev.yml`, `application-prod.yml`, `application-local.yml`
2. 更新基础配置`application.yml`，将认证相关配置设为可覆盖
3. 在`AuthController`中添加配置验证逻辑
4. 更新`JwtTokenUtil`的验证逻辑，考虑环境因素
5. 测试不同环境配置的正确性
6. 更新部署文档说明环境配置要求

## 风险评估与缓解

| 风险 | 可能性 | 影响 | 缓解措施 |
|------|--------|------|----------|
| 生产环境误启用模拟认证 | 低 | 高 | 启动验证 + 生产环境明确禁用配置 |
| 配置继承错误 | 中 | 中 | 明确的配置覆盖策略 + 启动测试 |
| 环境变量设置错误 | 中 | 中 | 默认安全配置 + 环境检测日志 |
| 密钥长度不足 | 高 | 高 | 生产环境强制验证 + 开发环境警告 |

## 验收标准

1. ✅ 开发环境可以启用模拟认证进行测试
2. ✅ 生产环境启动时如果启用模拟认证会失败
3. ✅ 不同环境的配置正确继承和覆盖
4. ✅ 启动日志清晰显示当前激活的profiles和配置状态
5. ✅ 生产环境强制启用JWT严格验证

## 后续优化建议

1. **配置加密**: 生产环境敏感配置使用加密存储
2. **配置中心**: 集成配置中心实现动态配置管理
3. **审计日志**: 记录配置变更和认证事件
4. **自动化测试**: 为不同环境配置添加自动化测试

---

**设计批准**: ✅ 用户已批准此设计方案
**设计时间**: 2026-03-20
**设计人员**: Claude Code
**关联任务**: 模拟认证环境限制方案实现