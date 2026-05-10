import type { LoginVO } from '@/types'
import { defineStore } from 'pinia'
import { ref } from 'vue'

// 认证信息 持久化存储
export const useAuthStore = defineStore(
	// 定义store id
	'auth',
	// 定义状态
	() => {
		const authInfo = ref<LoginVO>()

		const getAuthInfo = () => {
			return authInfo.value
		}

		const setAuthInfo = (newAuthInfo: LoginVO) => {
			authInfo.value = newAuthInfo
		}

		const removeAuthInfo = () => {
			authInfo.value = undefined
		}

		// 导出才能持久化
		return { authInfo, getAuthInfo, setAuthInfo, removeAuthInfo }
	},
	// 开启数据持久化存储
	{
		persist: true
	}
)
