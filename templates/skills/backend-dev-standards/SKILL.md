---
name: backend-dev-standards
description: Spring Boot 3.5 + Java 21 后端开发规范。当编写或审查后端代码（Controller、Service、Mapper、实体/DTO/VO、异常处理、配置等）时使用此 skill。
---

# 后端开发规范

## 触发条件

当用户编写、修改或审查以下文件时自动应用此规范：
- `*.java` 文件（com.example 包下）
- `pom.xml` Maven 配置文件
- `application*.yaml` / `application*.yml` 配置文件
- `*.sql` 数据库脚本
- `docker-compose.yaml` 部署文件
- `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

## 技术栈约定

| 类别 | 技术 | 版本 |
|------|------|------|
| 框架 | Spring Boot | 3.5.5 |
| 语言 | Java | 21（虚拟线程已启用）|
| 构建工具 | Maven | 多模块（common / domain / server）|
| ORM | MyBatis Plus | 3.5.13 |
| 数据库 | MySQL | 8.0+ |
| 连接池 | HikariCP（Spring Boot 内置）| - |
| 缓存（方案一） | Redis | 注解开启，可选 |
| 缓存（方案二） | hutool-cache (TimedCache) | 5.8.40 |
| 工具库 | Hutool（jwt, crypto, cache） | 5.8.40 |
| 接口文档 | Knife4j + SpringDoc OpenAPI | 4.4.0 / 2.8.0 |
| 参数校验 | Spring Validation (jakarta.validation) | - |
| 消息（方案一） | ApplicationEventPublisher（进程内异步） | - |
| 消息（方案二） | RabbitMQ | 注解开启，可选 |
| 邮件 | spring-boot-starter-mail | - |
| 短信 | 阿里云 dypnsapi | 2.0.0 |
| 简化代码 | Lombok | provided |

## 多模块分层架构

```
springboot/
├── common/       # 公共模块（不依赖 server，被其他模块依赖）
│   └── com.example.common
│       ├── autoconfig/     # 自动配置类（MyBatis Plus、WebMVC、消息等）
│       ├── constants/      # 常量（消息、正则、MQ、Redis 前缀）
│       ├── context/        # 线程上下文（UserContext）
│       ├── enums/          # 枚举（认证类型等）
│       ├── exceptions/     # 异常体系（Base→Biz/Db）
│       ├── handler/        # 全局异常处理器
│       ├── interceptor/    # 拦截器（JWT 校验）
│       ├── properties/     # 配置属性类（JWT、登录、阿里云等）
│       ├── result/         # 统一响应体（Result、PageDTO、PageQuery）
│       └── utils/          # 工具类（JWT 工具、缓存工具）
├── domain/       # 领域模型模块（依赖 common）
│   └── com.example.domain
│       ├── dto/            # 数据传输对象（请求参数）
│       ├── po/             # 持久化对象（数据库实体）
│       └── vo/             # 视图对象（响应数据）
└── server/       # 服务模块（依赖 common + domain）
    └── com.example.server
        ├── auth/           # 认证策略（策略模式）
        ├── controller/     # REST 控制器
        ├── mapper/         # MyBatis Plus Mapper 接口
        ├── mq/event/       # 事件定义（Java Record）
        ├── mq/listener/    # 事件监听器（@Async）
        └── service/impl/   # 服务接口与实现
```

**依赖关系：**
- `common` 不依赖其他模块
- `domain` 依赖 `common`
- `server` 依赖 `common` + `domain`

**规则：**
- 业务逻辑放在 `server` 模块，**不泄漏到 common 或 domain**
- domain 只放纯数据类（PO/DTO/VO），不包含业务方法
- common 只放可复用的组件、工具和配置，不包含业务逻辑

## REST API 设计规范

### URL 设计

- 使用 `@RequestMapping("/模块名")` 作为 Controller 级别前缀
- 资源用名词复数或不可数名词，如 `/auth`
- 版本号不放在 URL 中（无多版本需求）
- URL 全小写短横线分隔（kebab-case）: `/verify-code` 等

### 请求方法约定

| HTTP 方法 | 用途 | 示例 |
|-----------|------|------|
| GET | 查询（无请求体） | `GET /auth/login` |
| POST | 创建 / 提交操作 | `POST /auth/login`、`POST /auth/code` |
| PUT | 全量更新 | （暂未使用） |
| DELETE | 删除 | （暂未使用） |

### 统一响应格式

所有接口返回值必须包装为 `Result<T>`：

```java
// 成功 - code = 1
Result.success(data)

// 失败 - code = 0
Result.error("错误描述")
```

```json
// 成功响应
{ "code": 1, "msg": "success", "data": { ... } }

// 失败响应
{ "code": 0, "msg": "手机号格式错误", "data": null }
```

**规则：**
- 控制器方法必须返回 `Result<T>` 或 `Result<Void>`
- `code` 仅用 1（成功）和 0（失败），不用其他状态码
- `msg` 在失败时提供用户可读的中文错误描述

### 分页响应

使用 `PageQuery` 接收分页参数，`PageDTO<T>` 返回分页结果：

```java
// 查询参数
PageQuery query;  // { pageNo, pageSize, isAsc, sortBy }

// 转 MyBatis Plus 分页对象
Page<T> page = query.toMpPageDefaultSortByCreateTimeDesc();

// 返回分页结果
PageDTO<T> result = PageDTO.of(service.page(page));
```

分页响应结构：
```json
{ "code": 1, "msg": "success", "data": { "total": 100, "pages": 10, "list": [...] } }
```

### 参数校验

- 使用 `@Valid` / `@Validated` + Jakarta Validation 注解
- 校验失败由 `GlobalExceptionHandler` 统一处理并返回友好中文消息
- DTO 字段必填使用 `@NotNull`，值范围使用 `@Min` / `@Max`

```java
@PostMapping("/login")
public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) { ... }
```

### Knife4j 文档注解

每个 Controller 和接口方法必须添加文档注解：

```java
@Tag(name = "认证模块")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(...) { ... }
}
```

## 分层职责约定

### Controller 层

- 职责：接收请求、参数校验、调用 Service/Manager、返回响应
- **不包含业务逻辑**，业务逻辑委托给 Service 或策略类（如 AuthManager）
- 使用 `@RequiredArgsConstructor` + `final` 字段进行构造器注入
- 必须添加 `@Tag` 和 `@Operation` 注解

### Service 层

- 接口直接继承 `IService<Entity>`（MyBatis Plus 提供）
- 实现类继承 `ServiceImpl<Mapper, Entity>` 并实现 Service 接口
- 自定义业务方法定义在 Service 接口中，在 Impl 中实现
- 使用 `@Service` 和 `@RequiredArgsConstructor` 注解

### Mapper 层

- 接口继承 `BaseMapper<Entity>`
- 复杂查询优先使用 MyBatis Plus 的 Lambda 查询包装器
- 如需自定义 SQL，在 `resources/mapper/` 下创建 XML 映射文件

### 认证策略层

使用**策略模式**管理多种认证方式：

- `AuthStrategy` 接口定义统一的认证方法：
  - `AuthType getType()` — 返回认证类型
  - `User authenticate(LoginDTO)` — 执行认证
  - `void getVerifyCode(LoginDTO)` — 获取验证码
- `AuthManager` 作为策略调度器，持有 `Map<AuthType, AuthStrategy>`
- 每种策略通过 `@ConditionalOnProperty` 按配置条件加载
- 新增认证方式只需：实现 `AuthStrategy` + 添加 `@Component` + 配置开关

## 数据库操作规范

### 实体定义

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")       // 指定表名
public class User {
    @TableId(type = IdType.AUTO)  // 自增主键
    private Long id;
    private String username;
    private String password;
    private String phone;
    private String email;
}
```

**规则：**
- PO 类使用 Lombok：`@Data`、`@Builder`、`@NoArgsConstructor`、`@AllArgsConstructor`
- 表名通过 `@TableName` 显式指定（不用默认的驼峰转下划线）
- 主键策略 `IdType.AUTO`（数据库自增）
- 字段名使用驼峰命名，MyBatis Plus 自动转换为下划线

### 查询规范

- 优先使用 Lambda 查询包装器，**禁止字符串拼字段名**：

```java
// 正确
userService.lambdaQuery()
    .eq(User::getUsername, account)
    .one();

// 错误 — 不使用字符串字段名
userService.lambdaQuery()
    .eq("username", account)   // 编译期无法检查
    .one();
```

- 分页查询使用 `PageQuery.toMpPage()` 系列方法转换
- 默认按 `create_time DESC` 排序

### 数据库脚本规范

- SQL 脚本放在 `sql/init.sql`
- 使用 `CREATE TABLE IF NOT EXISTS`（幂等）
- 用户表必须包含 `id`（BIGINT AUTO_INCREMENT）、`create_time`、`update_time`
- 唯一约束字段：`username`、`phone`、`email`

## 异常处理规范

### 异常体系

```
Exception
  └── RuntimeException
        └── BaseException (abstract)
              ├── BizException     # 业务异常（用户可见错误）
              └── DbException      # 数据库异常
```

### 抛出异常

- 业务异常抛出 `BizException("用户可读的错误描述")`
- 数据库异常抛出 `DbException`，或使用 `DbException.of(SQLIntegrityConstraintViolationException)` 自动解析
- **禁止在 Controller 中 try-catch 返回 Result.error()**，统一由全局异常处理器处理

### 全局异常处理器

`GlobalExceptionHandler` (`@RestControllerAdvice`) 按优先级处理：

| 异常类型 | 处理方式 |
|---------|---------|
| `MethodArgumentNotValidException` | 提取字段校验错误信息，逗号拼接返回 |
| `ConstraintViolationException` | 提取约束错误信息，去重拼接返回 |
| `BizException` | 直接返回 `e.getMessage()` |
| `DbException` | 直接返回 `e.getMessage()` |
| `DuplicateKeyException` | 解析 MySQL 重复键消息，返回友好提示 |
| `HttpMessageNotReadableException` | 解析 JSON 反序列化错误（如非法枚举值）|
| `Exception`（保底） | 记录日志，返回"未知错误" |

**规则：**
- 所有异常处理器必须记录日志（`log.error(...)`）
- 保底处理器打印完整堆栈，其余可只打印消息

## 安全规范

### JWT 认证

- Token 存储在请求头中（header name 由 `example.jwt.token-name` 配置，默认 `token`）
- `JwtInterceptor` 拦截所有请求（`/**`），排除：
  - `/auth/**`（认证接口）
  - `/doc.html`、`/swagger-ui/**`、`/v3/api-docs/**`、`/webjars/**`（接口文档）
  - `/favicon.ico`、`/error`
- Token 校验失败返回 HTTP 401，前端自动跳转登录页并清除 token
- Token 默认有效期 86400 秒（24 小时），可配置

### 密码安全

- 密码使用 bcrypt 加密存储：`DigestUtil.bcrypt(password)`
- 密码校验使用 `DigestUtil.bcryptCheck(plain, encoded)`
- **禁止明文存储密码**

### 用户上下文

```java
// 设置当前用户（在拦截器中）
UserContext.setId(userId);

// 获取当前用户（在业务代码中）
Long userId = UserContext.getId();

// 清理（请求结束后在拦截器 afterCompletion 中自动执行）
UserContext.removeId();
```

- `UserContext` 基于 `ThreadLocal`，当前请求线程内有效
- **不要**在异步线程中访问 `UserContext`（ThreadLocal 无法传递）

### CORS 配置

- `WebMvcConfig` 中允许所有来源的跨域请求
- 允许的请求方法：GET、POST、PUT、DELETE、OPTIONS
- 预检请求缓存 3600 秒

## 异步与事件规范

### 进程内事件（当前方案）

使用 `ApplicationEventPublisher` 发布事件：

```java
// 1. 定义事件（Java Record）
public record SendEmailVerifyCodeEvent(String email) {}

// 2. 发布事件
eventPublisher.publishEvent(new SendEmailVerifyCodeEvent(email));

// 3. 监听事件
@Async
@EventListener(SendEmailVerifyCodeEvent.class)
public void listen(SendEmailVerifyCodeEvent event) { ... }
```

**规则：**
- 事件使用 Java Record 定义（不可变，语义清晰）
- 监听器方法加 `@Async` 实现异步处理
- 监听器中必须自行 try-catch 异常，避免影响主流程

### RabbitMQ（可选方案，代码已注释）

- 交换机：`user.topic`（Topic 类型）
- 消息路由键：`user.phone.code`、`user.email.code`
- 已在 `MqConstant` 中预定义常量，在监听器代码中以注释形式保留了 `@RabbitListener` 写法

## 日志规范

- 使用 Lombok `@Slf4j` 注解，**不要手动声明 Logger**
- 日志级别按包配置：`com.example: debug`
- 日志输出到文件：`logs/` 目录，日期格式 `yyyy-MM-dd HH:mm:ss`
- 关键节点必须记录日志：
  - 启动成功：`log.info("启动成功...")`
  - Bean 初始化：`log.info("配置Mybatis-Plus插件")`、`log.info("添加JWT拦截器")`
  - 请求处理：`log.info("jwt校验: {}", token)`
  - 异常捕获：`log.error("业务异常: {}", e.getMessage())` / `log.error("未知异常: ", e)`

**规则：**
- `info` 级别记录业务流程关键节点
- `debug` 级别记录调试信息
- `error` 级别记录异常，必须包含足够上下文
- 禁止在日志中输出密码、token 等敏感信息（token 仅在校验时可以记录）

## 配置规范

### 配置文件分层

```
application.yaml           # 公共配置（服务端口、编码、数据源、MyBatis Plus、日志）
application-dev.yaml       # 开发环境（本地数据库、开启 Knife4j）
application-prod.yaml      # 生产环境（Docker 网络、关闭 Knife4j、密码从环境变量读取）
```

### 自定义配置属性

使用 `@ConfigurationProperties` 定义类型安全的配置类：

```java
@Data
@ConfigurationProperties(prefix = "example.jwt")
public class JwtProperties {
    private String tokenName = "token";
    private String secretKey = "secretKey";
    private Long ttl = 24 * 60 * 60L;
}
```

**规则：**
- 配置前缀使用 `example.xxx` 格式（项目名前缀，避免与 Spring 内置冲突）
- 每个配置属性类使用 `@Data`，有默认值的不使用 Lombok `@NonNull`
- 嵌套配置使用静态内部类（如 `LoginProperties.WayProperties`）
- 敏感信息（密码、密钥、AccessKey）通过环境变量 `${ENV_VAR:default}` 注入，**不硬编码**

### 自动配置注册

Spring Boot 3.x 使用 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 注册自动配置类：

```
com.example.common.autoconfig.MybatisPlusConfig
com.example.common.autoconfig.WebMvcConfig
com.example.common.autoconfig.AliYunConfig
com.example.common.autoconfig.PropertiesConfig
com.example.common.autoconfig.MqConfig
```

### 条件装配

- 认证策略使用 `@ConditionalOnProperty` 按配置开关是否加载
- 自动配置类使用 `@ConditionalOnClass` 确保依赖存在时才装配
- `@ConditionalOnClass(WebMvcConfigurer.class)` — Spring Web 存在时
- `@ConditionalOnClass({MybatisPlusInterceptor.class, PaginationInnerInterceptor.class})` — MyBatis Plus 存在时

## 命名规范

### Java 命名

| 类别 | 规范 | 示例 |
|------|------|------|
| 包名 | 全小写，单数 | `com.example.common.autoconfig` |
| 类名 | PascalCase | `AuthController`、`BizException` |
| 接口 | PascalCase，不用 `I` 前缀 | `AuthStrategy`、`UserService` |
| 实现类 | 接口名 + `Impl` | `UserServiceImpl`、`SmsServiceImpl` |
| Entity/PO | 对应表名 PascalCase | `User`（表名 `user`）|
| DTO | 功能名 + `DTO` | `LoginDTO` |
| VO | 功能名 + `VO` | `LoginVO` |
| 常量类 | 接口（interface） | `MessageConstant`、`RegexConstant` |
| 配置属性类 | 领域 + `Properties` | `JwtProperties`、`LoginProperties` |
| 异常类 | 含义 + `Exception` | `BizException`、`DbException` |
| 枚举类 | PascalCase | `AuthType` |
| 管理器类 | 领域 + `Manager` | `AuthManager` |
| 工具类 | 领域 + `Util` | `JwtUtil`、`CacheUtil` |
| 配置类 | 中间件 + `Config` | `MybatisPlusConfig`、`WebMvcConfig` |
| 方法名 | camelCase | `parseJWT()`、`getVerfiyCode()` |
| 常量 | 全大写下划线分隔 | `USERNAME_PATTERN`、`UNKNOWN_ERROR` |

### 配置文件命名

| 文件 | 用途 |
|------|------|
| `application.yaml` | 公共配置 |
| `application-dev.yaml` | 开发环境配置 |
| `application-prod.yaml` | 生产环境配置 |
| `docker-compose.yaml` | 容器编排 |

### SQL 命名

- 数据库名和表名全部小写，单词用下划线分隔
- 字段名全部小写，下划线分隔
- 主键统一使用 `id`（BIGINT AUTO_INCREMENT）
- 时间字段：`create_time`、`update_time`

## 代码审查要点

1. **分层职责**：Controller 是否包含业务逻辑（禁止），domain 模块是否仅有数据类
2. **统一响应**：所有 Controller 方法是否返回 `Result<T>` 而非裸数据
3. **异常处理**：业务异常是否抛出 `BizException` 而非 `RuntimeException`，是否有 try-catch 吞异常
4. **参数校验**：DTO 是否使用了 `@NotNull`、`@Min` 等校验注解，Controller 是否加了 `@Valid`
5. **安全检查**：密码是否通过 bcrypt 加密，JWT 密钥是否可配置，敏感信息是否硬编码在配置文件中
6. **数据库查询**：是否使用 Lambda 查询而非字符串字段名，是否有 N+1 查询问题
7. **事务管理**：涉及多表写操作是否添加 `@Transactional`
8. **日志记录**：异常处是否有 `log.error`，关键操作是否有 `log.info`
9. **命名一致性**：是否遵循 DTO/VO/PO 后缀约定，包名是否符合分层规范
10. **依赖方向**：common 是否依赖了 domain 或 server（禁止逆依赖）
11. **配置外部化**：环境相关的配置是否抽取到 application-{profile}.yaml
12. **API 文档**：Controller 和接口方法是否添加了 `@Tag` / `@Operation` 注解
