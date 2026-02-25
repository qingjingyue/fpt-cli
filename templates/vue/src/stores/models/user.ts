import type { UserInfoVO } from '@/types/user'
import { defineStore } from 'pinia'
import { ref } from 'vue'

// 用户模块 userInfo 持久化存储
export const useUserStore = defineStore(
	// 定义store id
	'user',
	// 定义状态
	() => {
		// 导出才能持久化
		const userInfo = ref<UserInfoVO>()

		const getUserInfo = () => {
			return userInfo.value
		}

		const setUserInfo = (newUserInfo: UserInfoVO) => {
			userInfo.value = newUserInfo
		}

		const removeUserInfo = () => {
			userInfo.value = undefined
		}

		return { userInfo, getUserInfo, setUserInfo, removeUserInfo }
	},
	// 开启数据持久化存储
	{
		persist: true
	}
)
