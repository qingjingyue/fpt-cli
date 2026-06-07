import { createApp } from 'vue'
import Captcha from '@/components/captcha/Captcha.vue'

// 保存当前实例，以便后续可能的管理
let container: HTMLElement | null = null

function destroy(){
	if (container) {
		document.body.removeChild(container)
		container = null
	}
}

export const CaptchaBox = {
	show: async (): Promise<string> =>
		new Promise<string>((resolve, reject) => {
			// 如果已有实例，先销毁旧的
			destroy()

			// 1. 创建挂载点
			container = document.createElement('div')
			document.body.appendChild(container)

			// 2. 创建应用实例
			const app = createApp(Captcha, {
				onSuccess: (id: string) => {
					resolve(id)
					destroy()
				},
				onClose: () => {
					reject()
					destroy()
				}
			})

			// 3. 挂载组件
			app.mount(container)
		})
}
