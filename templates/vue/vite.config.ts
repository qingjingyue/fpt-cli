import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import Icons from 'unplugin-icons/vite'
import IconsResolver from 'unplugin-icons/resolver'

// https://vite.dev/config/
export default defineConfig({
	// 插件配置
	plugins: [
		vue(),
		vueDevTools(),
		// 按需自动导入
		AutoImport({
			resolvers: [
				// 自动导入  Element Plus 相关组件
				ElementPlusResolver(),
				// 自动导入图标组件
				IconsResolver({
					prefix: 'Icon'
				})
			]
		}),
		Components({
			resolvers: [
				// 自动导入 Element Plus 组件
				ElementPlusResolver(),
				// 自动注册图标组件
				IconsResolver({
					enabledCollections: ['ep']
				})
			]
		}),
		Icons({
			autoInstall: true
		})
	],
	resolve: {
		// 路径别名配置
		alias: {
			'@': fileURLToPath(new URL('./src', import.meta.url))
		}
	},
	// 基础路径配置 (默认值: '/') (打包时的静态资源路径前缀)
	base: '/', // Vite 会自动注入 import.meta.env.BASE_URL
	// 环境变量目录
	envDir: './env'
})
