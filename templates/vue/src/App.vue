<script setup lang="ts">
// 主题切换
import { useThemeStore } from '@/stores'
useThemeStore()
</script>

<template>
	<div>
		<router-view :key="$route.fullPath"></router-view>
	</div>
</template>

<style scoped></style>
