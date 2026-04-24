<template>
	<div class="split-container">
		<!-- 左侧图片区域 -->
		<div class="panel left">
			<div class="background-image"></div>
		</div>

		<!-- 右侧图片区域 -->
		<div class="panel right">
			<div class="login-form">
				<router-view></router-view>
			</div>
		</div>
	</div>
</template>

<script setup lang="ts">
// 计算 clip-path 的 polygon 值（关键！）
// 假设容器宽 W，高 H；斜线起点在 (0, H)，终点在 (W, slopeOffset * H)
// → polygon(0 0, W 0, W {slopeOffset*100}% , 0 {100 - slopeOffset*100}%)
const getClipPath = (side = 'left') => {
	const offset = -2
	if (side === 'left') {
		// 左侧：保留左下到斜线以上的部分
		return `polygon(0 0, 100% 0, 100% ${offset * 100}%, 0 ${100 - offset * 100}%)`
	} else {
		// 右侧：保留斜线以下到右上的部分
		return `polygon(0 ${100 - offset * 100}%, 0 100%, 100% 100%, 100% ${offset * 100}%)`
	}
}
</script>

<style scoped>
.split-container {
	position: relative;
	width: 100vw;
	height: 100vh;
	overflow: hidden;
}

.panel {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	display: flex;
	align-items: center;
	justify-content: center;
	transition: all 0.3s ease;
}

.left {
	clip-path: v-bind('getClipPath("left")');
}

.right {
	clip-path: v-bind('getClipPath("right")');
}

.background-image {
	width: 100%;
	height: 100%;
	object-fit: cover;
	background-size: cover;
	background-position: center;
	background-repeat: no-repeat;
	background-image: url('@/assets/login_bg.jpg');
	/* 内容向左偏 */
	transform: translateX(-30%);
}

.login-form {
	/* 内容向右偏 */
	transform: translateX(80%);
}
</style>
