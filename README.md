# fpt-cli

快速创建前后端分离项目的 CLI 脚手架工具。内置 Vue 3 + Spring Boot 项目模板，包含登录认证、主题切换、JWT 鉴权、API 文档、Docker 部署等开箱即用的功能，减少重复配置时间，专注于业务逻辑开发。

## 目录

- [前置依赖](#前置依赖)
- [安装](#安装)
- [快速开始](#快速开始)
- [命令参考](#命令参考)
  - [fpt create](#fpt-create)
  - [fpt clear](#fpt-clear)
- [交互式创建流程](#交互式创建流程)
- [模板项目](#模板项目)
  - [Vue 3 前端模板](#vue-3-前端模板)
  - [Spring Boot 后端模板](#spring-boot-后端模板)
- [生成的项目结构](#生成的项目结构)
- [Claude Code 开发规范 skills](#claude-code-开发规范-skills)
- [部署配置](#部署配置)
  - [GitHub Actions 自动部署](#github-actions-自动部署)
  - [Docker Compose 部署](#docker-compose-部署)
  - [阿里云短信服务（可选）](#阿里云短信服务可选)
  - [邮箱验证码服务（可选）](#邮箱验证码服务可选)
- [模板本地开发](#模板本地开发)
- [常见问题](#常见问题)
- [贡献指南](#贡献指南)

## 前置依赖

| 依赖 | 版本要求 | 说明 |
|------|----------|------|
| Node.js | >= 22.0.0 | CLI 运行环境 |
| pnpm | 最新版 | 前端包管理 |
| Java | >= 21 | 后端运行环境 |
| Maven | >= 3.8 | 后端构建工具 |
| MySQL | >= 8.0 | 数据库 |
| Docker | 可选 | 容器化部署 |

## 安装

```bash
npm install -g @qingjingyue/fpt-cli
```

验证安装：

```bash
fpt --version
```

## 快速开始

```bash
# 1. 创建项目目录（目录名只能包含小写字母）
mkdir myapp && cd myapp

# 2. 一键创建（网页端 + 服务端，用户名密码登录）
fpt create --default

# 3. 启动前端开发服务器
cd myapp-web && pnpm install && pnpm dev

# 4. 启动后端服务（需先配置 application-dev.yaml 中的数据库连接）
cd ../myapp-server && mvn spring-boot:run -pl server
```

## 命令参考

### fpt create

创建一个项目。

```
fpt create [options]
```

| 选项 | 说明 | 默认值 |
|------|------|--------|
| `--default` | 一键创建：web + server 端，账号密码登录，不使用 GitHub Actions，不生成 skills | — |
| `-e, --endpoint <ep>` | 要创建的端：`web`、`server` | 交互式选择 |
| `--login-way <way>` | 登录方式：`account`、`phone`、`email` | 交互式选择 |
| `--github-actions` | 生成 GitHub Actions 自动部署工作流 | `false` |
| `--skills` | 生成 Claude Code 开发规范 skills（`.claude/skills/`） | 交互式选择 |

**示例：**

```bash
# 交互式创建（推荐首次使用）
fpt create

# 只创建前端项目，手机号 + 邮箱登录，开启 skills
fpt create -e web --login-way phone --login-way email --skills

# 只创建后端项目，GitHub Actions 部署
fpt create -e server --github-actions

# 全量创建，所有登录方式，GitHub Actions + skills
fpt create -e all --login-way account --login-way phone --login-way email --github-actions --skills
```

### fpt clear

清空当前目录下的所有文件。

```bash
fpt clear
```

> 该命令会删除当前目录下的所有内容，**不可撤销**，请谨慎使用。

## 交互式创建流程

执行 `fpt create`（不带选项）时，CLI 会引导你逐步完成配置：

```
? 请选择要创建的端: (↑/↓ 切换, 空格选择, a 全选, 回车确认)
❯◉ 网页端
 ◯ 服务端

? 请选择登录方式: (↑/↓ 切换, 空格选择, a 全选, 回车确认)
❯◉ 用户名密码登录
 ◯ 手机号登录
 ◯ 邮箱登录

? 是否使用 GitHub Actions 自动部署项目？ (y/N)

? 是否生成 Claude Code 开发规范 skills（在 .claude/skills/ 目录下）？ (Y/n)
```

## 模板项目

### Vue 3 前端模板

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | ^3.5 | 前端框架（Composition API + `<script setup>`） |
| TypeScript | ~5.9 | 类型安全 |
| Vite | ^7.1 | 构建工具 |
| vue-router | ^4.6 | 路由（History 模式，懒加载） |
| Pinia | ^3.0 | 状态管理（含持久化插件） |
| Axios | ^1.13 | HTTP 请求（拦截器封装） |
| Element Plus | ^2.13 | UI 组件库（按需自动导入） |
| @vueuse/core | ^14.2 | 组合式工具函数 |
| unplugin-icons | ^23.0 | 图标自动导入 |

**内置功能：**

- Light / Dark 主题切换（CSS 变量 + `useDark`）
- 用户名密码登录 / 注册
- 手机号验证码登录
- 邮箱验证码登录
- useRequest 组合式函数（统一管理加载、错误、倒计时状态）
- Axios 请求/响应拦截器（自动注入 token、401 跳转登录、统一错误提示）
- Nginx 反向代理配置（Docker 部署用）

### Spring Boot 后端模板

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.5.5 | 后端框架（虚拟线程已启用） |
| Java | 21 | 运行环境 |
| MyBatis Plus | 3.5.13 | ORM（Lambda 查询、分页插件） |
| MySQL | 8.0+ | 数据库 |
| Hutool | 5.8.40 | 工具库（JWT、加密、缓存） |
| Knife4j | 4.4.0 | API 接口文档 |
| SpringDoc OpenAPI | 2.8.0 | OpenAPI 规范 |
| Lombok | — | 简化代码 |

**内置功能：**

- 策略模式多方式认证（账号 / 手机 / 邮箱，可按配置开关）
- JWT 令牌认证（Hutool JWT，可配置过期时间）
- BCrypt 密码加密
- 统一响应体 `Result<T>`（code: 1 成功 / 0 失败）
- 全局异常处理器（参数校验、业务异常、数据库异常分类处理）
- 分页查询封装（`PageQuery` + `PageDTO`）
- Knife4j 接口文档（中文，可按环境开关）
- 进程内异步事件（`ApplicationEventPublisher` + `@Async`）
- RabbitMQ 消息队列预留（已注释，按需启用）
- Redis 缓存预留（已注释，按需启用）

## 生成的项目结构

```
myapp/
├── .github/workflows/          # GitHub Actions 部署工作流（可选）
│   ├── deploy-web.yml
│   └── deploy-server.yml
├── myapp-web/                  # 前端项目
│   ├── .claude/skills/         # Claude Code 开发规范（可选）
│   ├── env/                    # 环境变量（.env.dev / .env.prod）
│   ├── src/
│   │   ├── apis/               # API 接口（Axios 实例 + 业务模块）
│   │   ├── assets/             # 静态资源
│   │   ├── components/         # 公共组件
│   │   ├── composables/        # 组合式函数（useRequest 等）
│   │   ├── router/             # 路由配置
│   │   ├── stores/             # Pinia 状态管理
│   │   ├── types/              # TypeScript 类型定义
│   │   ├── views/              # 页面组件
│   │   ├── App.vue
│   │   └── main.ts
│   ├── docker-compose.yaml     # Nginx 容器部署
│   ├── vite.config.ts
│   └── package.json
└── myapp-server/               # 后端项目
    ├── .claude/skills/         # Claude Code 开发规范（可选）
    ├── common/                 # 公共模块（配置、异常、拦截器、工具类）
    ├── domain/                 # 领域模块（PO / DTO / VO）
    ├── server/                 # 服务模块（Controller / Service / Mapper）
    ├── sql/                    # 数据库初始化脚本
    ├── docker-compose.yaml     # MySQL + App 容器部署
    └── pom.xml
```

## Claude Code 开发规范 skills

通过 `--skills` 选项可在生成的项目的 `.claude/skills/` 目录下创建开发规范文件，Claude Code 会根据这些规范审查和生成代码。

- **前端**：`frontend-dev-standards.md` — 涵盖 Vue 组件规范、状态管理、API 调用、样式约定、命名规范、代码审查要点
- **后端**：`backend-dev-standards.md` — 涵盖分层架构、REST API 设计、数据库操作、异常处理、安全规范、日志规范

```bash
# 生成 skills
fpt create --skills
```

## 部署配置

### GitHub Actions 自动部署

通过 `--github-actions` 选项生成部署工作流。推送代码到 GitHub 仓库的 main 分支时自动触发构建和部署。

**1. 在 GitHub 仓库配置 Secrets：**

```
Settings → Secrets and variables → Actions → New repository secret
```

| Secret | 说明 |
|--------|------|
| `SERVER_IP` | 服务器 IP 地址 |
| `SERVER_PORT` | SSH 端口（默认 22） |
| `SERVER_USER` | SSH 用户名 |
| `SERVER_PASSWORD` | SSH 密码 |

**2. 推送代码：**

```bash
git init
git add .
git commit -m "init"
git remote add origin <你的仓库地址>
git push -u origin main
```

### Docker Compose 部署

前端（Nginx）：

```bash
cd myapp-web
pnpm build:prod
docker compose up -d
```

后端（MySQL + App）：

```bash
cd myapp-server
mvn package -f pom.xml -pl server -am
docker compose up -d
```

### 阿里云短信服务（可选）

用于手机号验证码登录。前往[阿里云](https://ram.console.aliyun.com/manage/ak)申请 AccessKey，配置到服务器环境变量：

```bash
# ~/.profile
export ALIBABA_CLOUD_ACCESS_KEY_ID=<你的AccessKeyID>
export ALIBABA_CLOUD_ACCESS_KEY_SECRET=<你的AccessKeySecret>
```

### 邮箱验证码服务（可选）

用于邮箱验证码登录。配置 SMTP 服务器信息：

```bash
# ~/.profile
export MAIL_HOST=<你的邮箱服务地址>         例如: smtp.qq.com
export MAIL_USERNAME=<你的邮箱>
export MAIL_PASSWORD=<邮箱授权码>
```

## 模板本地开发

在 fpt-cli 项目目录下启动模板项目进行开发调试：

```bash
# 启动前端开发服务器（端口 5173）
pnpm dev-web

# 启动后端服务（端口 8080）
pnpm dev-server
```

后端需要本地 MySQL 数据库，默认连接 `localhost:3306`，用户名 `root`，密码 `123456`。可通过 `templates/springboot/server/src/main/resources/application-dev.yaml` 修改。

## 常见问题

### Q: 创建项目时报错"项目目录名称只能包含小写字母"

项目目录名必须全部为小写字母（a-z），不允许包含数字、连字符或大写字母。这是因为目录名会作为 Java 包名的一部分。

### Q: 前端启动报错 "Cannot find package 'xxx'"

进入前端项目目录后需先安装依赖：

```bash
cd myapp-web && pnpm install
```

### Q: 后端启动报错 "Access denied for user"

检查 MySQL 连接配置。默认连接信息在 `application-dev.yaml` 中：

```yaml
example:
  mysql:
    host: localhost
    password: 123456
```

### Q: Knife4j 接口文档无法访问

确认 `application-dev.yaml` 中 Knife4j 已开启：

```yaml
example:
  knife4j:
    production: false
```

访问地址：`http://localhost:8080/doc.html`

### Q: 验证码功能不可用

手机号验证码需要配置阿里云 AccessKey；邮箱验证码需要配置 SMTP 环境变量。详见上方部署配置章节。如不需要验证码登录，创建项目时不选择对应登录方式即可。

### Q: fpt clear 误删了文件

`fpt clear` 不可撤销。建议在执行前确认当前目录正确，并确保已提交或备份重要文件。

### Q: --default 模式下如何启用 skills

`--default` 会固定所有选项（skills = false）。如需启用 skills，使用非默认模式并显式传参：

```bash
fpt create -e all --login-way account --skills
```

## 贡献指南

欢迎提交 Issue 和 Pull Request。

### 项目结构

```
fpt-cli/
├── bin/fpt.js                  # CLI 入口
├── commands/                   # 命令实现
│   ├── create.js               #   创建主流程 + 交互式选项
│   ├── create-web.js           #   前端项目生成
│   ├── create-server.js        #   后端项目生成
│   ├── create-githubactions.js #   GitHub Actions 生成
│   └── clear.js                #   清理目录
├── templates/                  # 项目模板
│   ├── vue/                    #   Vue 3 前端模板
│   ├── springboot/             #   Spring Boot 后端模板
│   ├── skills/                 #   Claude Code 开发规范 skills
│   └── .github/workflows/     #   GitHub Actions 部署模板
├── utils/                      # 工具函数
│   ├── constants.js            #   路径常量
└── package.json
```

### 开发流程

```bash
# 克隆仓库
git clone https://github.com/qingjingyue/fpt-cli.git
cd fpt-cli

# 安装依赖
pnpm install

# 本地测试 CLI
node bin/fpt.js create --default
```
