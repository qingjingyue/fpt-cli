<template>
	<el-tab-pane label="邮箱登录" name="emailLogin">
		<el-form ref="formRef" :model="formData" :rules="rules" label-position="top">
			<el-form-item label="邮箱: " prop="email">
				<el-input v-model="formData.email" placeholder="请输入邮箱" />
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

// 定义邮箱登录表单引用
const formRef = ref<FormInstance>()

// 定义邮箱登录表单数据
const formData = ref({
	email: '',
	code: '',
	isRemember: false
})

// 定义邮箱登录表单校验规则
const rules = ref<FormRules<typeof formData.value>>({
	email: [
		{ required: true, message: '请输入邮箱', trigger: 'blur' },
		{
			validator: (rule, value, callback) => {
				if (/^[a-zA-Z0-9_%+-]+@[a-zA-Z0-9-]+\.[a-zA-Z]{2,}$/.test(value)) {
					callback()
				} else {
					callback('请输入正确的邮箱')
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

// 邮箱登录提交
const { loading: isLoading, run: login } = useRequest(async () => {
	// 校验表单
	await formRef.value?.validate()
	const { email, code } = formData.value
	const res = await authApi.login({
		authType: 'email',
		account: email,
		credential: code
	})
	useAuthStore().setAuthInfo(res)
	// 提示登录成功
	ElMessage.success('登录成功')
	// 跳转首页
	await router.push('/')
})

// 获取邮箱验证码
const {
	loading: isDisabled,
	countDown,
	run: getCode
} = useRequest(
	async () => {
		// 校验邮箱
		await formRef.value?.validateField('email')
		// 发送验证码
		const { email } = formData.value
		await authApi.getVerifyCode({ authType: 'email', account: email })
	},
	{ loadTime: 60 }
)
</script>

<style scoped></style>
