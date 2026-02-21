/**
 * 账密登录/注册请求参数
 */
export type AccountLoginDTO = {
    username: string
    password: string
}

/**
 * 手机号登录请求参数
 */
export type PhoneLoginDTO = {
    phone: string
    code: string
}

/**
 * 登录响应数据
 */
export type UserInfoVO = {
    id: number
    avatar: string
    username: string
    token: string
}
