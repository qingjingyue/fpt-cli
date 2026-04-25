import http from '@/apis/http'
import type { UserInfoVO } from '@/types/user'

/// 用户相关接口
export const userApi = {
	/// 账号登录接口
	loginByAccount: (data: { account: string; password: string }) => {
		return http.post<UserInfoVO>('/user/login/account', data)
	},

	/// 手机号登录接口
	loginByPhone: (data: { phone: string; code: string }) => {
		return http.post<UserInfoVO>('/user/login/phone', data)
	},

	/// 邮箱登录接口
	loginByEmail: (data: { email: string; code: string }) => {
		return http.post<UserInfoVO>('/user/login/email', data)
	},

	/// 获取验证码接口
	getVerifyCode: (type: 'phone' | 'email', value: string) => {
		return http.get<void>(`/user/login/code`, {
			params: {
				type,
				value
			}
		})
	}
}
