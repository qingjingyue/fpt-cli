<script setup lang="ts">
import { nextTick, onMounted, onUnmounted, ref } from 'vue'

const props = defineProps<{
	onSuccess: (id: string) => void
	onClose: () => void
}>()

// 验证码的一些配置和验证的回调
const config = {
	// 生成接口, 必选项, 必须配置
	requestCaptchaDataUrl: `${import.meta.env.VITE_API_BASE_URL}/captcha/generate`,
	// 验证接口, 必选项, 必须配置
	validCaptchaUrl: `${import.meta.env.VITE_API_BASE_URL}/captcha/check`,
	// 验证码绑定的 div 块, 必选项, 必须配置
	bindEl: '#captcha-box',
	// 验证成功回调函数, 必选项, 必须配置
	validSuccess: (response, configuration, tac) => {
		// 销毁验证码服务
		tac.destroyWindow()
		close()
		// 验证成功后调用后端接口
		props.onSuccess(response.data.id)
	},
	// 验证失败的回调函数, 可忽略, 如果不自定义 validFail 方法时，会使用默认的验证失败的函数
	validFail: (response, configuration, tac) => {
		tac.reloadCaptcha()
	},
	// 刷新按钮回调事件
	btnRefreshFun: (el, tac) => {
		tac.reloadCaptcha()
	},
	// 关闭按钮回调事件
	btnCloseFun: (el, tac) => {
		tac.destroyWindow()
		close()
		props.onClose()
	}
}

const style = {
	// 按钮样式
	btnUrl: 'https://minio.tianai.cloud/public/captcha-btn/btn3.png',
	// 背景样式
	// bgUrl: 'https://minio.tianai.cloud/public/captcha-btn/btn3-bg.jpg',
	bgUrl: 'src/background/yellow.png',
	// logo地址
	logoUrl: 'https://minio.tianai.cloud/public/static/captcha/images/logo.png',
	// 滑动边框样式
	moveTrackMaskBgColor: '#f7b645',
	moveTrackMaskBorderColor: '#ef9c0d'
}

const visible = ref(false)
// 对外暴露关闭方法
const close = () => {
	visible.value = false
}

onMounted(async () => {
	visible.value = true
	await nextTick()
	// 第一个参数是tac文件的目录地址, 目录里包含tac的js和css等文件
	// 第二个参数是tac验证码的相关配置
	// 第三个参数是tac窗口的一些样式
	window
		.initTAC('./tac', config, style)
		.then((tac) => {
			tac.init()
		})
		.catch((error) => {
			console.log('initTAC fail:', error)
		})
	// new TAC(config, style).init();
})

onUnmounted(() => {
	visible.value = false
})
</script>

<template>
	<!-- 遮罩层：全屏、半透明背景，flex 居中其子元素 -->
	<div class="captcha-mask" v-if="visible">
		<!-- 验证码的实际容器，用于挂载 tianai-captcha -->
		<div id="captcha-box" class="captcha-container"></div>
	</div>
</template>

<style scoped>
/* 遮罩层：覆盖全屏，黑色半透明背景，flex 居中子元素 */
.captcha-mask {
	position: fixed;
	top: 0;
	left: 0;
	width: 100vw;
	height: 100vh;
	background-color: rgba(0, 0, 0, 0.5); /* 半透明遮罩 */
	display: flex;
	justify-content: center;
	align-items: center;
	z-index: 9999; /* 确保遮罩在最上层 */
}

/* 验证码容器：可设置固定宽度，或由内部 SDK 撑开 */
.captcha-container {
	/* 根据需要设置宽度，例如 350px，也可不设由内部决定 */
	/* width: 350px; */
	/* 添加一点圆角或阴影，提升美观度 */
	border-radius: 8px;
	box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}
</style>
