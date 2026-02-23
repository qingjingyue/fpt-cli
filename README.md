# fpt-cli

这是一个快速创建项目的cli工具,项目模板中包含了常用的项目配置,减少重复配置的时间,从而专注于项目的业务逻辑开发.
同时,使用了github actions,实现了项目的自动构建和部署.

目前提供的项目模板有:

- vue3 + ts
    - 包管理工具 pnpm
    - 构建工具 vite
    - 路由 vue-router
    - 状态管理 pinia
    - 网络请求 axios
    - 组件库 element-plus
    - nginx

- springboot3.5 + java21
    - 构建工具 maven
    - 数据库 mysql + mybatis-plus
    - 缓存 redis
    - 消息队列 rabbitmq

# 使用方法

1. 安装 cli 工具

```bash
npm install -g fpt-cli
```

2. 创建项目

```bash
fpt create <project-name> -t vue
```

```bash
fpt create <project-name> -t springboot
```

# 项目配置

## vue 模板

### 目录结构

```
   vue
    ├── .vscode             # vscode 配置文件, 包含了常用的插件和设置
    ├── conf.d              # nginx 配置文件, 包含了项目的 nginx 配置
    ├── public              # 包含favicon.ico
    ├── src                 # 项目源代码目录
    │   ├── apis                # 后端接口
    │   ├── assets              # 静态资源
    │   ├── components          # 组件
    │   ├── router              # 路由配置
    │   ├── store               # 状态管理
    │   ├── types               # 类型定义
    │   ├── utils               # 工具函数
    │   ├── views               # 视图组件
    │   ├── App.vue
    │   ├── main.ts
    ├── .env.dev                # 开发环境配置文件
    ├── .env.prod               # 生产环境配置文件
    ├── env.d.ts                # 环境变量类型定义文件
    ├── docker-compose.yml      # 用于 docker 部署, 搭配conf.d
    ├── index.html
    ├── package.json
    ├── pnpm-lock.yaml
    ├── tsconfig.json
    ├── vite.config.ts
```

### 功能实现

- 请求响应拦截器
- 账号密码登录注册
- 手机号验证码登录注册
- light - dark 主题切换

## springboot 模板

### 目录结构

```
springboot
    ├── common                  # 公共模块, 包含了常用的类, 常量, 枚举, 异常, 拦截器, 工具类, 配置类, 自动配置等
    ├── domain                  # 领域模型模块, 包含了项目的实体类, dto, po, vo等
    ├── server                  # 服务模块, 包含了项目的controller, service, mapper, mq.listener等
    │   ├── src/main/resources
    │                   ├── application.yml          # 公共配置文件
    │                   ├── application-dev.yml      # 开发环境配置文件
    │                   ├── application-prod.yml     # 生产环境配置文件
    ├── sql                     # 数据库目录, 用于初始化数据库
    ├── docker-compose.yml      # 用于 docker 部署, 搭配sql
    ├── pom.xml                 # 包含了项目的依赖和配置

```

### 功能实现

- 账号密码登录注册
- 手机号验证码登录注册
- JWT 认证
- 全局异常处理
- 统一响应体Result
- Knife4j 接口文档
