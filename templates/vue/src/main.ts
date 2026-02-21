import { createApp } from 'vue'
import App from './App.vue'
import pinia from './stores'
import router from './router'
// 引入 Element Plus 样式
import 'element-plus/dist/index.css'
// 引入 Element Plus 暗黑主题样式
import 'element-plus/theme-chalk/dark/css-vars.css'
// 引入自定义全局样式
import '@/assets/main.css'

const app = createApp(App)
app.use(pinia)
app.use(router)

app.mount('#app')
