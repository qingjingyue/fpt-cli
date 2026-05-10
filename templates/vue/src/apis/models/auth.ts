import http from '@/apis/http'
import type { LoginDTO, LoginVO } from '@/types'

/// 认证相关接口
export const authApi = {

	/// 获取登录方式
	getLoginWay: () => {
		return http.get<string[]>('/auth/login')
	},

	/// 登录接口
	login: (data: LoginDTO) => {
		return http.post<LoginVO>('/auth/login', data)
	},

	/// 获取验证码接口
	getVerifyCode: (data: LoginDTO) => {
		return http.post<void>(`/auth/code`, data)
	}

}
