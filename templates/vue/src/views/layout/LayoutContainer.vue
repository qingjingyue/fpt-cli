<script setup lang="ts">
import { ref } from 'vue'
import { Sunny, Moon } from '@element-plus/icons-vue'

// 主题切换
import { useThemeStore } from '@/stores'
const themeStore = useThemeStore()

// 登录状态
import { useAuthStore } from '@/stores'
const authStore = useAuthStore()
const authInfo = authStore.getAuthInfo()
const isLogin = ref(authInfo?.id !== undefined)

// 退出登录
const logout = () => {
	authStore.removeAuthInfo()
	// isLogin.value = false
	// 刷新页面
	window.location.reload()
}
</script>

<template>
	<div>
		<el-container>
			<el-header style="padding: 0 60px">
				<el-row justify="space-between" align="middle">
					<el-col span="auto">
						<!-- <el-image style="height: 60px" src="src/assets/logo.png" fit="cover" /> -->
						logo
					</el-col>
					<el-col span="auto">
						<el-row :gutter="20" justify="center" align="middle">
							<el-col span="auto">
								<el-menu mode="horizontal" :ellipsis="false" router>
									<el-menu-item index="1" route="/index">home</el-menu-item>
									<el-menu-item index="2" route="/about">about</el-menu-item>
								</el-menu>
							</el-col>
							<el-col span="auto" v-if="isLogin">
								<el-dropdown>
									<div
										style="display: flex; align-items: center; cursor: pointer"
									>
										<el-avatar
											:src="
												authInfo?.avatar || 'src/assets/default_avatar.png'
											"
										/>
										<span style="margin-left: 5px">
											{{ authInfo?.username }}
										</span>
									</div>
									<template #dropdown>
										<el-dropdown-menu>
											<el-dropdown-item @click="logout"
												>退出登录</el-dropdown-item
											>
										</el-dropdown-menu>
									</template>
								</el-dropdown>
							</el-col>
							<el-col span="auto" v-else>
								<router-link to="/login">登录</router-link>
							</el-col>
							<el-col span="auto">
								<el-switch
									v-model="themeStore.theme"
									:active-action-icon="Sunny"
									:inactive-action-icon="Moon"
									@change="themeStore.toggleTheme"
									style="--el-switch-on-color: gray; --el-switch-off-color: gray"
								/>
							</el-col>
						</el-row>
					</el-col>
				</el-row>
			</el-header>
			<el-main>
				<router-view></router-view>
			</el-main>
			<el-footer>Footer</el-footer>
		</el-container>
	</div>
</template>

<style scoped></style>
