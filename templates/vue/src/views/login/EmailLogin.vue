<template>
	<el-tab-pane label="邮箱登录" name="emailLogin">
		<el-form ref="formRef" :model="formData" :rules="rules" label-position="top">
			<el-form-item label="邮箱: " prop="email">
				<el-input v-model="formData.email" placeholder="请输入邮箱" />
			</el-form-item>
			<el-form-item label="验证码: " prop="code">
				<el-input v-model="formData.code" placeholder="请输入验证码">
					<template #append>
						<el-button :disabled="isDisabled" @click="getCode">
							{{ isCodeSent ? `${countDown}秒后重新获取` : '获取验证码' }}
						</el-button>
					</template>
				</el-input>
			</el-form-item>
			<el-form-item prop="isRemember">
				<el-checkbox v-model="formData.isRemember" label="记住我" />
			</el-form-item>
			<el-form-item>
				<el-button
					:loading="isLoading"
					type="primary"
					style="width: 100%"
					@click="handleLogin"
				>
					登录
				</el-button>
			</el-form-item>
		</el-form>
	</el-tab-pane>
</template>

<script setup lang="ts">
import { useVerificationLogin } from '@/composables/useVerificationLogin'

const {
	formRef,
	formData,
	rules,
	isLoading,
	isDisabled,
	isCodeSent,
	countDown,
	getCode,
	handleLogin
} = useVerificationLogin({ type: 'email' })
</script>

<style scoped></style>
