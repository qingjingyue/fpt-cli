<script setup lang="ts"></script>

<template>
    <div class="login-layout">
        <div class="page-content">
            <router-view></router-view>
        </div>
    </div>
</template>

<style scoped>
.login-layout {
    background-size: cover;
    background-image: url('@/assets/login_bg.jpg');
}
.page-content {
    width: 100vw;
    height: 100vh;
    /* 水平垂直居中 */
    display: flex;
    align-items: center;
    justify-content: center;
}
</style>
