---
name: frontend-dev-standards
description: Vue 3 + TypeScript 前端开发规范。当编写或审查前端代码（Vue组件、Pinia store、Axios请求、TypeScript类型）时使用此 skill。
---

# 前端开发规范

## 触发条件

当用户编写、修改或审查以下文件时自动应用此规范：
- `*.vue` 文件
- `src/apis/**` 目录下的 API 模块
- `src/stores/**` 目录下的 Pinia store
- `src/composables/**` 目录下的组合式函数
- `src/types/**` 目录下的类型定义
- `src/router/**` 目录下的路由配置
- `vite.config.ts`、`tsconfig.*.json` 等配置文件

## 技术栈约定

| 类别 | 技术 | 版本 |
|------|------|------|
| 框架 | Vue | ^3.5 |
| 语言 | TypeScript | ~5.9 |
| 构建工具 | Vite | ^7.1 |
| 包管理 | pnpm | - |
| 路由 | vue-router | ^4.6 |
| 状态管理 | Pinia | ^3.0 |
| 持久化 | pinia-plugin-persistedstate | ^4.7 |
| HTTP 请求 | Axios | ^1.13 |
| UI 组件库 | Element Plus | ^2.13 |
| 工具函数 | @vueuse/core | ^14.2 |
| 图标 | @element-plus/icons-vue | ^2.3 |
| 自动导入 | unplugin-auto-import / unplugin-vue-components | - |
| 代码质量 | ESLint + Prettier | - |

## 目录结构约定

```
src/
├── apis/              # API 接口层
│   ├── http.ts        # Axios 实例、拦截器、统一响应类型
│   ├── models/        # 按业务模块拆分的 API 文件（如 auth.ts）
│   └── index.ts       # 统一导出（export * from './models/xxx'）
├── assets/            # 静态资源（图片、全局样式）
├── components/        # 可复用组件
├── composables/       # 组合式函数（hooks）
│   ├── models/        # 按功能拆分的 hook 文件
│   └── index.ts       # 统一导出
├── router/            # 路由配置
│   └── index.ts       # 路由定义（懒加载）
├── stores/            # Pinia 状态管理
│   ├── models/        # 按模块拆分的 store 文件
│   └── index.ts       # 创建 Pinia 实例 + 统一导出
├── types/             # TypeScript 类型定义
│   ├── models/        # 按业务模块拆分的 .d.ts 文件
│   └── index.ts       # 统一导出
├── utils/             # 工具函数
├── views/             # 页面组件（按路由路径组织）
│   ├── login/         # 登录相关页面
│   ├── layout/        # 布局组件
│   └── index/         # 首页
├── App.vue            # 根组件
└── main.ts            # 应用入口
```

**规则：**
- 每个 `models/` 子目录下按业务模块建文件，不允许将所有逻辑堆在一个文件
- 每个目录的 `index.ts` 负责统一导出该目录下 `models/` 的内容
- 页面组件按路由路径分目录存放

## 组件开发规范

### 组件结构

组件必须使用 `<script setup lang="ts">` 语法，按以下顺序组织：

```vue
<template>
  <!-- 模板内容 -->
</template>

<script setup lang="ts">
// 1. 导入依赖
import { ref } from 'vue'
import { authApi } from '@/apis'
import { useAuthStore } from '@/stores'
import { useRequest } from '@/composables'

// 2. 定义响应式状态
const formData = ref({ ... })

// 3. 定义校验规则（如有表单）
const rules = ref<FormRules>({ ... })

// 4. 定义方法（使用 useRequest 包装异步操作）
const { loading: isLoading, run: submit } = useRequest(async () => {
  // 业务逻辑
})
</script>

<style scoped>
/* 组件样式 */
</style>
```

### 表单组件规范

- 表单引用必须带类型：`ref<FormInstance>()`
- 表单数据使用 `ref()` 包裹对象
- 校验规则使用 `ref<FormRules<typeof formData.value>>()`
- 提交前必须调用 `await formRef.value?.validate()`
- 所有异步操作使用 `useRequest` 包装，获取 `loading` 和 `run` 状态

### Element Plus 使用规范

- 组件已通过 `unplugin-vue-components` 实现按需自动导入，**无需手动 import**
- 图标使用 `@element-plus/icons-vue`，需要手动 import 具体图标组件
- 本地化配置在 `App.vue` 中通过 `ElConfigProvider` 统一设置
- 主题切换通过 `useDark` / `useToggle` 配合 CSS 变量实现

## 状态管理规范

### Store 定义

使用 Pinia Composition API 风格（`defineStore` + `setup` 函数）：

```typescript
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useXxxStore = defineStore(
  'xxx',           // store id，唯一
  () => {           // setup 函数
    const data = ref<SomeType>()

    const getData = () => data.value
    const setData = (val: SomeType) => { data.value = val }

    // 必须 return 需要暴露的状态和方法
    return { data, getData, setData }
  },
  {
    persist: true   // 如需持久化则开启
  }
)
```

**规则：**
- Store ID 必须唯一且语义化
- 状态使用 `ref()` 定义
- 通过 getter/setter 函数操作状态，不直接暴露可变引用
- 需要客户端持久化的数据开启 `persist: true`（如 auth token、theme 偏好）
- 所有 store 在 `stores/index.ts` 中通过 `export *` 统一导出

## 样式编写规范

- 必须使用 `scoped` 属性避免样式污染
- 主题色通过 CSS 变量实现，定义在 `assets/main.css`：

```css
:root {
  --font-color: #000000;
  --bg-color: rgba(255, 255, 255, 0.3);
}
:root.dark {
  --font-color: #ffffff;
  --bg-color: rgba(0, 0, 0, 0.3);
}
```

- 组件中引用 CSS 变量：`var(--bg-color)`
- 全局样式在 `main.ts` 中导入 Element Plus 主题和 `assets/main.css`

## API 调用规范

### HTTP 实例配置

所有请求通过 `@/apis/http.ts` 中的 Axios 实例发起，已配置：
- 请求拦截器：自动注入 token（从 `authStore.getAuthInfo()?.token` 获取）
- 响应拦截器：自动处理 `code === 1`（成功，返回 data）和 `code === 0`（失败，提示 msg）
- 401 自动跳转登录页并清除无效 token

### 统一响应类型

后端统一响应格式为：
```typescript
type Result<T> = {
  code: 0 | 1   // 1: 成功, 0: 失败
  msg: string
  data: T
}
```

响应拦截器已在成功时自动摘取 `data`，因此 API 调用直接返回业务数据类型，**无需手动解包**。

### API 模块定义

每个业务模块在 `apis/models/` 下定义一个 API 对象：

```typescript
import http from '@/apis/http'
import type { LoginDTO, LoginVO } from '@/types'

export const authApi = {
  login: (data: LoginDTO) => http.post<LoginVO>('/auth/login', data),
  getVerifyCode: (data: LoginDTO) => http.post<void>('/auth/code', data),
}
```

**规则：**
- API 对象使用字面量对象（非 class），以模块命名：`xxxApi`
- 每个方法对应一个后端接口
- 必须声明请求和响应的泛型类型
- 接口路径不使用模板字符串拼接参数（除少数情况），参数通过请求体传递

### 组件中调用 API

```typescript
const { loading: isSubmitting, run: submit } = useRequest(async () => {
  await formRef.value?.validate()
  const res = await authApi.login({ ...formData.value })
  useAuthStore().setAuthInfo(res)
  ElMessage.success('操作成功')
  await router.push('/')
})
```

## useRequest 组合式函数

项目中封装的 `useRequest` 用于统一处理异步操作的加载状态：

```typescript
function useRequest<T>(
  requestFn: () => Promise<T>,
  options?: { loadTime?: number }  // 倒计时秒数（用于验证码按钮）
)

// 返回
{
  loading: Ref<boolean>      // 加载状态
  countDown: Ref<number>     // 倒计时秒数
  error: Ref<Error | null>   // 错误信息
  data: ShallowRef<T>        // 响应数据
  run: () => Promise<...>    // 执行函数
}
```

**规则：**
- 所有异步操作（表单提交、数据获取）必须使用 `useRequest` 包装
- 通过解构重命名获取语义明确的变量（如 `loading: isSubmitting`、`run: submit`）
- 验证码获取场景使用 `loadTime: 60` 实现倒计时

## 类型定义规范

- 类型文件放在 `types/models/` 下，使用 `.d.ts` 扩展名
- DTO（请求）和 VO（响应）分别用 `LoginDTO` / `LoginVO` 命名
- 类型使用 `type` 定义（非 `interface`）
- 注释使用 `///` 三斜线风格
- 所有类型在 `types/index.ts` 中统一导出

## 命名规范

| 类别 | 规范 | 示例 |
|------|------|------|
| Vue 组件文件 | PascalCase | `PhoneLogin.vue`、`LayoutContainer.vue` |
| 页面组件 | 描述性命名 | `HomePage.vue`、`NotFound.vue` |
| TS/JS 模块文件 | camelCase | `useRequest.ts`、`auth.ts` |
| API 对象 | `xxxApi` | `authApi` |
| Store ID | 短横线分隔 | `auth`、`theme` |
| Store 导出函数 | `useXxxStore` | `useAuthStore`、`useThemeStore` |
| 组合式函数 | `useXxx` | `useRequest` |
| 类型/接口 | PascalCase | `LoginDTO`、`LoginVO` |
| Ref 变量 | camelCase | `formData`、`isLoading` |
| 事件处理函数 | 动词开头 | `login`、`getCode`、`submit` |
| 表单引用 | `xxxRef` 或 `xxxFormRef` | `formRef`、`loginFormRef` |
| 加载状态 | `isXxx` / `isXxxLoading` | `isLoading`、`isDisabled` |

## 路由规范

- 路由使用懒加载：`component: () => import('@/views/...')`
- 嵌套路由通过 `children` 配置
- 404 路由使用 `path: '/:pathMatch(.*)*'`
- 路由切换时自动滚动到顶部：`scrollBehavior() { return { top: 0 } }`

## 代码审查要点

1. **类型安全**：所有变量、函数参数和返回值必须有明确类型，禁止使用 `any`
2. **错误处理**：异步操作是否使用 `useRequest` 包装，是否处理了 401 场景
3. **样式隔离**：组件样式是否设置了 `scoped`
4. **持久化合理性**：敏感数据不应持久化，token 等认证信息使用 `persist: true`
5. **API 调用规范**：是否复用了 `http.ts` 中的 Axios 实例，是否正确声明了响应泛型
6. **组件拆分**：单个组件不超过 300 行，复杂逻辑抽取为 composable
7. **表单校验**：是否在提交前调用了 `validate()`
8. **导入路径**：使用 `@/` 别名，避免深层相对路径
9. **无冗余注释**：代码自解释，不写 "what" 注释（如 `// 定义状态`）
