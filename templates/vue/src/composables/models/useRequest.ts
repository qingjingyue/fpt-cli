import { ref, shallowRef } from 'vue'

/**
 * 处理请求的加载状态
 * @param requestFn 请求函数
 * @param options 配置项
 * @param options.loadTime 指定加载时间
 * @returns
 */
export function useRequest<T>(requestFn: () => Promise<T>, options?: { loadTime?: number }) {
	const loading = ref(false)
	const error = ref<Error | null>(null)
	const data = shallowRef<T | undefined>()

	const countDown = ref(0)

	const run = async () => {
		if (options?.loadTime) {
			countDown.value = options.loadTime
			// 开启倒计时
			const timer = setInterval(() => {
				countDown.value--
				loading.value = true
				if (countDown.value <= 0) {
					clearInterval(timer)
					loading.value = false
				}
			}, 1000)
		}

		loading.value = true
		error.value = null
		try {
			data.value = await requestFn()
			return data
		} catch (err) {
			error.value = err as Error
			countDown.value = -1
			throw err
		} finally {
			loading.value = false
		}
	}

	return {
		loading,
		countDown,
		error,
		data,
		run
	}
}
