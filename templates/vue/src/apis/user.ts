import http from '@/apis/http'
import type { UserInfoVO } from '@/types/user'

/**
 * 手机号登录接口
 * @param data 登录请求参数
 * @returns 登录响应数据
 */
export const loginByPhone = (data: { phone: string; code: string }) => {
	return http.post<UserInfoVO>('/user/login/phone', data)
}

/**
 * 获取验证码接口
 * @param type 验证码类型
 * @param value 手机号或邮箱
 * @returns 验证码响应数据
 */
export const getVerifyCode = (type: 'phone' | 'email', value: string) => {
	return http.get<void>(`/user/login/code`, {
		params: {
			type,
			value
		}
	})
}

/**
 * 邮箱登录接口
 * @param data 登录请求参数
 * @returns 登录响应数据
 */
export const loginByEmail = (data: { email: string; code: string }) => {
	return http.post<UserInfoVO>('/user/login/email', data)
}
