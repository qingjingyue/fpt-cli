import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useDark, useToggle } from '@vueuse/core'

// 主题模块 theme 持久化存储
export const useThemeStore = defineStore(
	// 定义store id
	'theme',
	// 定义状态
	() => {
		const theme = ref(useDark())

		const toggleTheme = () => {
			useToggle(theme)
		}

		// 导出才能持久化
		return { theme, toggleTheme }
	},
	// 开启数据持久化存储
	{
		persist: true
	}
)
