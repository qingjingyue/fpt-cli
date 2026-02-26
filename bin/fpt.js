#!/usr/bin/env node

import { program } from 'commander'
import { createCommand } from '../commands/create.js'

// 捕获未处理的异常
process.on('uncaughtException', (error) => {
	console.error(error.message)
	process.exit(1)
})

// 定义 CLI 程序
program.name('fpt').description('快速创建项目').version('1.0.0')

// 创建项目命令
program.command('create <endpoint>').description('创建一个项目的某一端').action(createCommand)

// 解析命令行参数
program.parse()
