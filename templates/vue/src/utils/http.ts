import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios'
import router from '@/router'
import { ElMessage } from 'element-plus'
import { userUserStore } from '@/stores'

// 后端统一响应类型
export type Result<T = never> = {
	code: 0 | 1
	msg: string
	data: T
}

// 扩展Axios的request方法类型
interface HttpInstance extends AxiosInstance {
	get<T = unknown>(url: string, config?: AxiosRequestConfig): Promise<T>
	post<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T>
	put<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T>
	delete<T = unknown>(url: string, config?: AxiosRequestConfig): Promise<T>
}

// 自定义axios实例
const http = axios.create({
	// 基础URL配置
	baseURL: import.meta.env.VITE_API_BASE_URL,
	// 请求超时配置
	timeout: 10000,
}) as HttpInstance

// 添加请求拦截器
http.interceptors.request.use(
	(request) => {
		// 请求统一携带token
		const token = userUserStore().getUserInfo()?.token ?? false
		if (token) {
			request.headers['token'] = token
		}
		return request
	},
	(error) => Promise.reject(error),
)

// 添加响应拦截器
http.interceptors.response.use(
	(response: AxiosResponse<Result>) => {
		// 成功状态码为 1, 摘取核心响应数据
		if (response.data.code === 1) {
			return response.data.data
		}
		// 失败状态码为 0, 触发错误事件
		if (response.data.code === 0) {
			ElMessage.error(response.data.msg || '服务器繁忙,请稍后重试')
		}
		// 处理业务失败
		return Promise.reject(response.data)
	},
	(error) => {
		// 处理401错误
		// 错误特殊情况 => 401 权限不足 或 token过期 => 强制跳转到登录页
		if (error.response.status === 401) {
			// 清空无效用户信息
			userUserStore().removeUserInfo()
			// 强制跳转到登录页
			router.push('/login')
		}
		// 默认错误情况 => 给用户提示
		ElMessage.error(error.response.data.msg || '服务器繁忙,请稍后重试')

		// 默认错误情况 => 给用户提示
		return Promise.reject(error)
	},
)

export default http
