<template>
	<el-tab-pane :label="isRegister ? '账号注册' : '账号登录'" name="accountLogin">
		<el-form
			v-if="isRegister"
			ref="registerFormRef"
			:model="registerForm"
			:rules="registerRules"
			label-position="top"
		>
			<el-form-item label="账号: " prop="account">
				<el-input v-model="registerForm.account" placeholder="请输入用户名"> </el-input>
			</el-form-item>
			<el-form-item label="密码: " prop="password">
				<el-input v-model="registerForm.password" placeholder="请输入密码"> </el-input>
			</el-form-item>
			<el-form-item label="确认密码: " prop="doublePassword">
				<el-input v-model="registerForm.doublePassword" placeholder="请确认密码">
				</el-input>
			</el-form-item>
			<el-form-item>
				<el-button
					:loading="isRegisterLoading"
					type="primary"
					style="width: 100%"
					@click="register"
					>注册
				</el-button>
			</el-form-item>
			<el-form-item>
				<div style="width: 100%; display: flex; justify-content: flex-end">
					<el-link @click="isRegister = !isRegister">去登录</el-link>
				</div>
			</el-form-item>
		</el-form>

		<el-form
			v-if="!isRegister"
			ref="loginFormRef"
			:model="loginForm"
			:rules="loginRules"
			label-position="top"
		>
			<el-form-item label="账号: " prop="account">
				<el-input v-model="loginForm.account" placeholder="请输入用户名"> </el-input>
			</el-form-item>
			<el-form-item label="密码: " prop="password">
				<el-input v-model="loginForm.password" placeholder="请输入密码"> </el-input>
			</el-form-item>
			<el-form-item prop="isRemember">
				<el-checkbox v-model="loginForm.isRemember" label="记住我" />
			</el-form-item>
			<el-form-item>
				<el-button
					:loading="isLoginLoading"
					type="primary"
					style="width: 100%"
					@click="login"
					>登录
				</el-button>
			</el-form-item>
			<el-form-item>
				<div style="width: 100%; display: flex; justify-content: flex-end">
					<el-link @click="isRegister = !isRegister">去注册</el-link>
				</div>
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

// 定义是否注册状态
const isRegister = ref(false)

// -----------------------------------------------------------------------------------------------------------

// 定义注册表单引用
const registerFormRef = ref<FormInstance>()

// 定义注册表单数据
const registerForm = ref({
	account: '',
	password: '',
	doublePassword: ''
})

// 定义注册表单校验规则
const registerRules = ref<FormRules<typeof registerForm.value>>({
	account: [
		{ required: true, message: '请输入账号', trigger: 'blur' },
		{ min: 6, max: 12, message: '长度必须为 6-12 个字符', trigger: 'blur' }
	],
	password: [
		{ required: true, message: '请输入密码', trigger: 'blur' },
		{ min: 6, max: 12, message: '长度必须为 6-12 个字符', trigger: 'blur' }
	],
	doublePassword: [
		{ required: true, message: '请确认密码', trigger: 'blur' },
		{
			validator: (rule, value, callback) => {
				if (value !== registerForm.value.password) {
					callback('两次密码不一致')
				} else {
					callback()
				}
			},
			trigger: 'blur'
		}
	]
})

// 账号注册表单提交
const { loading: isRegisterLoading, run: register } = useRequest(async () => {
	// 校验表单
	await registerFormRef.value?.validate()
	const { account, password } = registerForm.value
	await authApi.login({
		login: false,
		authType: 'account',
		account,
		credential: password
	})
	// 提示注册成功
	ElMessage.success('注册成功')
	// 跳转登录
	isRegister.value = false
})

// -----------------------------------------------------------------------------------------------------------

// 定义注册登录表单引用
const loginFormRef = ref<FormInstance>()

// 定义账号登录表单数据
const loginForm = ref({
	account: '',
	password: '',
	isRemember: false
})

// 定义账号登录表单校验规则
const loginRules = ref<FormRules<typeof loginForm.value>>({
	account: [
		{ required: true, message: '请输入账号', trigger: 'blur' },
		{ min: 6, max: 12, message: '长度必须为 6-12 个字符', trigger: 'blur' }
	],
	password: [
		{ required: true, message: '请输入密码', trigger: 'blur' },
		{ min: 6, max: 12, message: '长度必须为 6-12 个字符', trigger: 'blur' }
	]
})

// 账号登录提交
const { loading: isLoginLoading, run: login } = useRequest(async () => {
	// 校验表单
	await loginFormRef.value?.validate()
	const { account, password } = loginForm.value
	const res = await authApi.login({
		login: true,
		authType: 'account',
		account,
		credential: password
	})
	useAuthStore().setAuthInfo(res)
	// 提示登录成功
	ElMessage.success('登录成功')
	// 跳转首页
	await router.push('/')
})
</script>

<style scoped></style>
