import http from '@/utils/http'
import type { AccountLoginDTO, PhoneLoginDTO, UserInfoVO } from '@/types/user'

/**
 * 用户注册接口
 * @param data 注册请求参数
 * @returns 注册响应数据
 */
export const registerByAccount = (data: AccountLoginDTO) => {
    return http.post<void>('/user/register', data)
}

/**
 * 账密登录接口
 * @param data 登录请求参数
 * @returns 登录响应数据
 */
export const loginByAccount = (data: AccountLoginDTO) => {
    return http.post<UserInfoVO>('/user/login/account', data)
}

/**
 * 手机号登录接口
 * @param data 登录请求参数
 * @returns 登录响应数据
 */
export const loginByPhone = (data: PhoneLoginDTO) => {
    return http.post<UserInfoVO>('/user/login/phone', data)
}

/**
 * 获取手机号验证码接口
 * @param phone 手机号
 * @returns 验证码响应数据
 */
export const getPhoneCode = (phone: string) => {
    return http.get<void>(`/user/login/phone/code?phone=${phone}`)
}
