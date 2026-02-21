import { createPinia } from 'pinia'
// 引入 pinia 插件 pinia-plugin-persistedstate 实现数据持久化存储
import persist from 'pinia-plugin-persistedstate'

const pinia = createPinia()
pinia.use(persist)

export * from './models/user'
export default pinia
