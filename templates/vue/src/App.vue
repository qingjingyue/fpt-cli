<script setup lang="ts"></script>

<template>
	<div>
		<router-view :key="$route.fullPath"></router-view>
	</div>
</template>

<style scoped></style>
