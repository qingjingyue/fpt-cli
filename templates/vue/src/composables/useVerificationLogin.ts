import { ref, reactive, onUnmounted } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { authApi } from '@/apis'
import { useAuthStore } from '@/stores'

type LoginType = 'email' | 'phone'

interface UseVerificationLoginOptions {
	type: LoginType
}

// 根据类型获取对应的字段名、正则、提示信息、API参数类型
const getConfigByType = (type: LoginType) => {
	if (type === 'email') {
		return {
			fieldName: 'email',
			pattern: /^[a-zA-Z0-9_%+-]+@[a-zA-Z0-9-]+\.[a-zA-Z]{2,}$/,
			message: '请输入正确的邮箱',
			codeType: 'email' as const
		}
	} else {
		return {
			fieldName: 'phone',
			pattern: /^1[3-9]\d{9}$/,
			message: '请输入正确的手机号',
			codeType: 'phone' as const
		}
	}
}

export function useVerificationLogin(options: UseVerificationLoginOptions) {
	const { type } = options
	const config = getConfigByType(type)

	// 表单引用
	const formRef = ref<FormInstance>()

	// 动态表单数据，字段名由 config.fieldName 决定
	const formData = reactive({
		[config.fieldName]: '',
		code: '',
		isRemember: false
	})

	// 表单校验规则
	const rules = reactive<FormRules>({
		[config.fieldName]: [
			{
				required: true,
				message: `请输入${type === 'email' ? '邮箱' : '手机号'}`,
				trigger: 'blur'
			},
			{
				validator: (_rule, value, callback) => {
					if (config.pattern.test(value)) {
						isDisabled.value = false
						callback()
					} else {
						isDisabled.value = true
						callback(config.message)
					}
				},
				trigger: 'blur'
			}
		],
		code: [
			{ required: true, message: '请输入验证码', trigger: 'blur' },
			{ min: 6, max: 6, message: '长度必须为 6 个数字', trigger: 'blur' }
		]
	})

	// 登录提交 loading 状态
	const isLoading = ref(false)

	// 获取验证码按钮禁用状态
	const isDisabled = ref(true)

	// 验证码是否已发送（用于按钮文本切换）
	const isCodeSent = ref(false)

	// 倒计时秒数
	const countDown = ref(60)

	let timer: ReturnType<typeof setInterval> | null = null

	// 获取验证码
	const getCode = async () => {
		// 校验账号字段
		await formRef.value?.validateField(config.fieldName)
		if (isCodeSent.value) return

		const accountValue = formData[config.fieldName] as string
		await authApi.getVerifyCode({
			authType: config.codeType,
			account: accountValue
		})

		isCodeSent.value = true
		isDisabled.value = true

		timer = setInterval(() => {
			countDown.value--
			if (countDown.value <= 0) {
				clearInterval(timer!)
				timer = null
				isCodeSent.value = false
				isDisabled.value = false
				countDown.value = 60
			}
		}, 1000)
	}

	// 登录提交
	const handleLogin = async () => {
		await formRef.value?.validate()
		isLoading.value = true

		try {
			const accountValue = formData[config.fieldName] as string
			const { code } = formData

			const res = await authApi.login({
				authType: config.codeType,
				account: accountValue,
				credential: code
			})

			useAuthStore().setAuthInfo(res)
			ElMessage.success('登录成功')
			await router.push('/')
		} catch (error) {
			console.error(error)
		} finally {
			isLoading.value = false
		}
	}

	// 组件卸载时清除倒计时定时器
	onUnmounted(() => {
		if (timer) clearInterval(timer)
	})

	return {
		formRef,
		formData,
		rules,
		isLoading,
		isDisabled,
		isCodeSent,
		countDown,
		getCode,
		handleLogin
	}
}
