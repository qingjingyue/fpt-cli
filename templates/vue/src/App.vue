<script setup lang="ts">
// 主题切换
import { useThemeStore } from '@/stores'
useThemeStore()
// 全局配置本地化设置
import { ElConfigProvider } from 'element-plus'
// 修改 Element Plus 组件使用中文
import zhCn from 'element-plus/es/locale/lang/zh-cn'
</script>

<template>
	<div>
		<el-config-provider :locale="zhCn">
			<router-view :key="$route.fullPath"></router-view>
		</el-config-provider>
	</div>
</template>

<style scoped></style>
