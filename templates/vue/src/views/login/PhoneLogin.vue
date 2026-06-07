<template>
	<el-tab-pane label="手机号登录" name="phoneLogin">
		<el-form ref="formRef" :model="formData" :rules="rules" label-position="top">
			<el-form-item label="手机号: " prop="phone">
				<el-input v-model="formData.phone" placeholder="请输入手机号">
					<template #prepend> +86 </template>
				</el-input>
			</el-form-item>
			<el-form-item label="验证码: " prop="code">
				<el-input v-model="formData.code" placeholder="请输入验证码">
					<template #append>
						<el-button :disabled="isDisabled" @click="getCode">{{
							isDisabled ? `${countDown}秒后重新获取` : '获取验证码'
						}}</el-button>
					</template>
				</el-input>
			</el-form-item>
			<el-form-item prop="isRemember">
				<el-checkbox v-model="formData.isRemember" label="记住我" />
			</el-form-item>
			<el-form-item>
				<el-button :loading="isLoading" type="primary" style="width: 100%" @click="login"
					>登录
				</el-button>
			</el-form-item>
		</el-form>
	</el-tab-pane>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import router from '@/router'
import { authApi } from '@/apis'
import { useAuthStore } from '@/stores'
import { useRequest } from '@/composables'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { CaptchaBox } from '@/components/captcha'

// 定义手机号登录表单引用
const formRef = ref<FormInstance>()

// 定义手机号登录表单数据
const formData = ref({
	phone: '',
	code: '',
	isRemember: false
})

// 定义手机号登录表单校验规则
const rules = ref<FormRules<typeof formData.value>>({
	phone: [
		{ required: true, message: '请输入手机号', trigger: 'blur' },
		{
			validator: (rule, value, callback) => {
				if (/^1[3-9]\d{9}$/.test(value)) {
					callback()
				} else {
					callback('请输入正确的手机号')
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

//手机号登录提交
const { loading: isLoading, run: login } = useRequest(async () => {
	// 校验表单
	await formRef.value?.validate()
	const id = await CaptchaBox.show()
	const { phone, code } = formData.value
	const res = await authApi.login({
		authType: 'phone',
		account: phone,
		credential: code,
		behaviorCaptchaId: id
	})
	useAuthStore().setAuthInfo(res)
	// 提示登录成功
	ElMessage.success('登录成功')
	// 跳转首页
	await router.push('/')
})

// 获取验证码
const {
	loading: isDisabled,
	countDown,
	run: getCode
} = useRequest(
	async () => {
		// 校验手机号
		await formRef.value?.validateField('phone')
		// 发送验证码
		const { phone } = formData.value
		await authApi.getVerifyCode({ authType: 'phone', account: phone })
	},
	{ loadTime: 60 }
)
</script>

<style scoped></style>
