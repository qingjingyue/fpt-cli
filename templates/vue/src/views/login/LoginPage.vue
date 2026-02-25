<script setup lang="ts">
import { loginByPhone, getVerifyCode, loginByEmail } from '@/apis/user'
import { type FormRules, type FormInstance, ElMessage } from 'element-plus'
import { ref } from 'vue'
import { useUserStore } from '@/stores'
import router from '@/router'

// 定义登录选项卡
const activeTab = ref('phoneLogin')

// -----------------------------------------------------------------------------------

// 定义手机号登录表单引用
const phoneLoginFormRef = ref<FormInstance>()

// 定义手机号登录表单数据
const phoneLoginForm = ref({
	phone: '',
	code: '',
	isRemember: false
})

// 定义手机号登录表单校验规则
const phoneLoginRules = ref<FormRules<typeof phoneLoginForm.value>>({
	phone: [
		{ required: true, message: '请输入手机号', trigger: 'blur' },
		{
			validator: (rule, value, callback) => {
				if (/^1[3-9]\d{9}$/.test(value)) {
					isDisabled.value = false
					callback()
				} else {
					isDisabled.value = true
					callback('请输入正确的手机号')
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

//手机号登录提交
const phoneLogin = async () => {
	// 校验表单
	await phoneLoginFormRef.value?.validate()
	// 登录提交loading状态
	isLoading.value = true
	const timer = setTimeout(() => {
		isLoading.value = false
		clearTimeout(timer)
	}, 2000)
	const { phone, code } = phoneLoginForm.value
	const res = await loginByPhone({ phone, code })
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

// 获取验证码
const getPhoneCode = async () => {
	// 校验手机号
	await phoneLoginFormRef.value?.validateField('phone')
	// 60秒内只能获取一次验证码
	if (isCodeSent.value) {
		return
	}
	// 发送验证码
	const { phone } = phoneLoginForm.value
	await getVerifyCode('phone', phone)
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

// -----------------------------------------------------------------------------------

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
	const res = await loginByEmail({ email, code })
	useUserStore().setUserInfo(res)
	// 登录提交loading状态
	isLoading.value = false
	// 提示登录成功
	ElMessage.success('登录成功')
	// 跳转首页
	router.push('/')
}

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
	await getVerifyCode('email', email)
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

<template>
	<div class="login-tabs">
		<el-row>
			<el-col :span="4">
				<p style="font-size: 24px; font-weight: bold; margin-bottom: 20px">登录</p>
			</el-col>
		</el-row>

		<el-tabs v-model="activeTab">
			<el-tab-pane label="手机号登录" name="phoneLogin">
				<el-form
					ref="phoneLoginFormRef"
					:model="phoneLoginForm"
					:rules="phoneLoginRules"
					label-position="top"
					class="el-form"
				>
					<el-form-item label="手机号: " prop="phone">
						<el-input v-model="phoneLoginForm.phone" placeholder="请输入手机号">
							<template #prepend> +86 </template>
						</el-input>
					</el-form-item>
					<el-form-item label="验证码: " prop="code">
						<el-input v-model="phoneLoginForm.code" placeholder="请输入验证码">
							<template #append>
								<el-button :disabled="isDisabled" @click="getPhoneCode">{{
									isCodeSent ? `${countDown}秒后重新获取` : '获取验证码'
								}}</el-button>
							</template>
						</el-input>
					</el-form-item>
					<el-form-item prop="isRemember">
						<el-checkbox v-model="phoneLoginForm.isRemember" label="记住我" />
					</el-form-item>
					<el-form-item>
						<el-button
							:loading="isLoading"
							type="primary"
							style="width: 100%"
							@click="phoneLogin"
							>登录
						</el-button>
					</el-form-item>
				</el-form>
			</el-tab-pane>
			<el-tab-pane label="邮箱登录" name="emailLogin">
				<el-form
					ref="emailLoginFormRef"
					:model="emailLoginForm"
					:rules="emailLoginRules"
					label-position="top"
					class="el-form"
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
		</el-tabs>
	</div>
</template>

<style scoped>
.login-tabs {
	width: 500px;
	padding: 50px;
	background-color: var(--bg-color);
}
.el-form {
	min-height: 300px;
}
</style>
