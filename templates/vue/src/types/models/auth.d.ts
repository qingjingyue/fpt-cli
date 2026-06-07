/// 登录请求数据
export type LoginDTO = {
	login?: boolean
	authType: 'account' | 'phone' | 'email'
	account: string
	credential?: string
	behaviorCaptchaId?: string
}

/// 登录响应数据
export type LoginVO = {
	id: number
	avatar: string
	username: string
	token: string
}
