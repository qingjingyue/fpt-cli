/// <reference types="vite/client" />

// 给ImportMetaEnv类型追加自定义环境变量, 来自.env文件
interface ImportMetaEnv {
	// 为自定义的环境变量定义类型
	readonly VITE_APP_TITLE: string
	readonly VITE_API_BASE_URL: string
}
