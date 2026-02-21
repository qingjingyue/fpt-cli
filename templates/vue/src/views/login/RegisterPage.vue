<script setup lang="ts">
import { registerByAccount } from '@/apis/user'
import { type FormRules, type FormInstance, ElMessage } from 'element-plus'
import { ref } from 'vue'
import { ArrowRight } from '@element-plus/icons-vue'

// 定义登录选项卡

// 定义账密登录表单引用
const accountRegisterFormRef = ref<FormInstance>()

// 定义账密登录表单数据
const accountRegisterForm = ref({
	username: '',
	password: '',
	// 再次确认密码
	confirmPassword: '',
	// 是否同意注册协议
	isAgree: false,
})

// 定义账密登录表单校验规则
const accountRegisterRules = ref<FormRules<typeof accountRegisterForm.value>>({
	username: [
		{ required: true, message: '请输入用户名', trigger: 'blur' },
		{ min: 6, max: 16, message: '长度必须在 6~16个字符', trigger: 'blur' },
	],
	password: [
		{ required: true, message: '请输入密码', trigger: 'blur' },
		{ min: 6, max: 16, message: '长度必须在 6~16个字符', trigger: 'blur' },
	],
	// 校验两次密码是否一致
	confirmPassword: [
		{ required: true, message: '请确认密码', trigger: 'blur' },
		{
			validator: (rule, value, callback) => {
				if (value !== accountRegisterForm.value.password) {
					callback(new Error('两次输入密码不一致'))
				} else {
					callback()
				}
			},
			trigger: 'blur',
		},
	],
	isAgree: [{ pattern: /^true$/, message: '请同意', trigger: 'change' }],
})

/**
 * 账密注册提交
 */
const accountRegister = async () => {
	// 校验表单
	await accountRegisterFormRef.value?.validate()
	const { username, password } = accountRegisterForm.value
	await registerByAccount({ username, password })
	// 提示注册成功
	ElMessage.success('注册成功')
}
</script>

<template>
	<div class="login-tabs">
		<el-row>
			<el-col :span="4">
				<p
					style="
						font-size: 24px;
						font-weight: bold;
						margin-bottom: 20px;
					"
				>
					注册
				</p>
			</el-col>
			<el-col :span="6" :offset="14" style="align-content: center">
				<router-link
					to="/login"
					style="
						display: flex;
						align-items: center;
						justify-content: end;
					"
					>前往登录<el-icon><ArrowRight /></el-icon
				></router-link>
			</el-col>
		</el-row>

		<el-form
			ref="accountRegisterFormRef"
			:model="accountRegisterForm"
			:rules="accountRegisterRules"
			label-position="top"
			class="el-form"
		>
			<el-form-item label="用户名: " prop="username">
				<el-input
					v-model="accountRegisterForm.username"
					placeholder="请输入用户名"
				></el-input>
			</el-form-item>
			<el-form-item label="密码: " prop="password">
				<el-input
					v-model="accountRegisterForm.password"
					placeholder="请输入密码"
					type="password"
					show-password
				></el-input>
			</el-form-item>
			<el-form-item label="确认密码: " prop="confirmPassword">
				<el-input
					v-model="accountRegisterForm.confirmPassword"
					placeholder="请确认密码"
					type="password"
					show-password
				></el-input>
			</el-form-item>
			<el-form-item prop="isAgree">
				<el-checkbox
					v-model="accountRegisterForm.isAgree"
					label="我已阅读并同意 用户协议、隐私政策"
				/>
			</el-form-item>
			<el-form-item>
				<el-button
					type="primary"
					style="width: 100%"
					@click="accountRegister"
					>注册
				</el-button>
			</el-form-item>
		</el-form>
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
