<template>
	<el-tab-pane label="邮箱登录" name="emailLogin">
		<el-form
			ref="emailLoginFormRef"
			:model="emailLoginForm"
			:rules="emailLoginRules"
			label-position="top"
		>
			<el-form-item label="邮箱: " prop="email">
				<el-input v-model="emailLoginForm.email" placeholder="请输入邮箱" />
			</el-form-item>
			<el-form-item label="验证码: " prop="code">
				<el-input v-model="emailLoginForm.code" placeholder="请输入验证码">
					<template #append>
						<el-button :disabled="isDisabled" @click="getEmailCode">{{
							isCodeSent ? `${countDown}秒后重新获取` : '获取验证码'
						}}</el-button>
					</template>
				</el-input>
			</el-form-item>
			<el-form-item prop="isRemember">
				<el-checkbox v-model="emailLoginForm.isRemember" label="记住我" />
			</el-form-item>
			<el-form-item>
				<el-button
					:loading="isLoading"
					type="primary"
					style="width: 100%"
					@click="emailLogin"
					>登录
				</el-button>
			</el-form-item>
		</el-form>
	</el-tab-pane>
</template>

<script setup lang="ts">
import router from '@/router'
import { useUserStore } from '@/stores'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { ref } from 'vue'
import { userApi } from '@/apis/user'

// 定义邮箱登录表单引用
const emailLoginFormRef = ref<FormInstance>()

// 定义邮箱登录表单数据
const emailLoginForm = ref({
	email: '',
	code: '',
	isRemember: false
})

// 定义邮箱登录表单校验规则
const emailLoginRules = ref<FormRules<typeof emailLoginForm.value>>({
	email: [
		{ required: true, message: '请输入邮箱', trigger: 'blur' },
		{
			validator: (rule, value, callback) => {
				if (/^[a-zA-Z0-9_%+-]+@[a-zA-Z0-9-]+\.[a-zA-Z]{2,}$/.test(value)) {
					isDisabled.value = false
					callback()
				} else {
					isDisabled.value = true
					callback('请输入正确的邮箱')
				}
			}
		}
	],
	code: [
		{ required: true, message: '请输入验证码', trigger: 'blur' },
		{ min: 6, max: 6, message: '长度必须为 6 个数字', trigger: 'blur' }
	]
})

// 登录提交loading状态
const isLoading = ref(false)

// 邮箱登录提交
const emailLogin = async () => {
	// 校验表单
	await emailLoginFormRef.value?.validate()
	// 登录提交loading状态
	isLoading.value = true
	const timer = setTimeout(() => {
		isLoading.value = false
		clearTimeout(timer)
	}, 2000)
	const { email, code } = emailLoginForm.value
	const res = await userApi.loginByEmail({ email, code })
	useUserStore().setUserInfo(res)
	// 登录提交loading状态
	isLoading.value = false
	// 提示登录成功
	ElMessage.success('登录成功')
	// 跳转首页
	router.push('/')
}

// 获取验证码按钮是否禁用
const isDisabled = ref(true)

// 验证码发送状态
const isCodeSent = ref(false)

// 验证码倒计时
const countDown = ref(60)

// 获取邮箱验证码
const getEmailCode = async () => {
	// 校验邮箱
	await emailLoginFormRef.value?.validateField('email')
	// 60秒内只能获取一次验证码
	if (isCodeSent.value) {
		return
	}
	// 发送验证码
	const { email } = emailLoginForm.value
	await userApi.getVerifyCode('email', email)
	// 验证码发送状态切换为已发送
	isCodeSent.value = true
	// 禁用获取验证码按钮
	isDisabled.value = true
	// 开启倒计时
	const timer = setInterval(() => {
		countDown.value--
		isDisabled.value = true
		if (countDown.value <= 0) {
			clearInterval(timer)
			// 倒计时结束后，验证码发送状态切换为未发送
			isCodeSent.value = false
			// 启用获取验证码按钮
			isDisabled.value = false
			// 重置倒计时
			countDown.value = 60
		}
	}, 1000)
}
</script>

<style scoped></style>
