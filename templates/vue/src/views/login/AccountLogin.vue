<template>
	<el-tab-pane label="账号登录" name="accountLogin">
		<el-form
			ref="accountLoginFormRef"
			:model="accountLoginForm"
			:rules="accountLoginRules"
			label-position="top"
		>
			<el-form-item label="账号: " prop="account">
				<el-input v-model="accountLoginForm.account" placeholder="请输入账号"> </el-input>
			</el-form-item>
			<el-form-item label="密码: " prop="password">
				<el-input v-model="accountLoginForm.password" placeholder="请输入密码"> </el-input>
			</el-form-item>
			<el-form-item prop="isRemember">
				<el-checkbox v-model="accountLoginForm.isRemember" label="记住我" />
			</el-form-item>
			<el-form-item>
				<el-button
					:loading="isLoading"
					type="primary"
					style="width: 100%"
					@click="accountLogin"
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

// 定义账号登录表单引用
const accountLoginFormRef = ref<FormInstance>()

// 定义账号登录表单数据
const accountLoginForm = ref({
	account: '',
	password: '',
	isRemember: false
})

// 定义账号登录表单校验规则
const accountLoginRules = ref<FormRules<typeof accountLoginForm.value>>({
	account: [
		{ required: true, message: '请输入账号', trigger: 'blur' },
		{ min: 6, max: 12, message: '长度必须为 6-12 个字符', trigger: 'blur' }
	],
	password: [
		{ required: true, message: '请输入密码', trigger: 'blur' },
		{ min: 6, max: 12, message: '长度必须为 6-12 个字符', trigger: 'blur' }
	]
})

// 登录提交loading状态
const isLoading = ref(false)

//账号登录提交
const accountLogin = async () => {
	// 校验表单
	await accountLoginFormRef.value?.validate()
	// 登录提交loading状态
	isLoading.value = true
	const timer = setTimeout(() => {
		isLoading.value = false
		clearTimeout(timer)
	}, 2000)
	const { account, password } = accountLoginForm.value
	const res = await userApi.loginByAccount({ account, password })
	useUserStore().setUserInfo(res)
	// 登录提交loading状态
	isLoading.value = false
	// 提示登录成功
	ElMessage.success('登录成功')
	// 跳转首页
	router.push('/')
}
</script>

<style scoped></style>
