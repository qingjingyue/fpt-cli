import { createRouter, createWebHistory } from 'vue-router'

// 路由配置
const router = createRouter({
    // 路由模式
    history: createWebHistory(import.meta.env.BASE_URL),
    // 路由规则
    routes: [
        {
            path: '/login',
            component: () => import('@/views/login/LoginLayout.vue'),
            children: [
                {
                    path: '/login',
                    component: () => import('@/views/login/LoginPage.vue'),
                },
                {
                    path: '/register',
                    component: () => import('@/views/login/RegisterPage.vue'),
                },
            ],
        },
        {
            path: '/',
            component: () => import('@/views/layout/LayoutContainer.vue'),
            redirect: '/index',
            children: [
                {
                    path: '/index',
                    component: () => import('@/views/index/HomePage.vue'),
                },
                {
                    path: '/about',
                    component: () => import('@/views/about/AboutPage.vue'),
                },
            ],
        },
        // 404 路由
        { path: '/:pathMatch(.*)*', component: () => import('@/views/NotFound.vue') },
    ],
    // 路由切换时滚动到顶部
    scrollBehavior() {
        return {
            top: 0,
        }
    },
})

export default router
