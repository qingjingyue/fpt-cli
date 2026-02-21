<script setup lang="ts">
import { Sunny, Moon } from '@element-plus/icons-vue'
import { useDark, useToggle } from '@vueuse/core'

// 主题切换
const isDark = useDark()
const toggleTheme = useToggle(isDark)
</script>

<template>
    <div>
        <el-container>
            <el-header>
                <el-row :gutter="20" justify="center" align="middle">
                    <el-col span="auto"> logo </el-col>
                    <el-col span="auto" :offset="14"> </el-col>
                    <el-col span="auto">
                        <el-menu mode="horizontal" :ellipsis="false" router>
                            <el-menu-item index="1" route="/index">home</el-menu-item>
                            <el-menu-item index="2" route="/about">about</el-menu-item>
                        </el-menu>
                    </el-col>
                    <el-col span="auto">
                        <router-link to="/login">登录</router-link>
                    </el-col>
                    <el-col span="auto">
                        <router-link to="/register">注册</router-link>
                    </el-col>
                    <el-col span="auto">
                        <el-switch
                            v-model="isDark"
                            :active-action-icon="Sunny"
                            :inactive-action-icon="Moon"
                            @change="toggleTheme"
                            style="--el-switch-on-color: gray; --el-switch-off-color: gray"
                        />
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
