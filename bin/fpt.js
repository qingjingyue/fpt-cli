#!/usr/bin/env node

import { program } from 'commander'
import { createCommand } from '../commands/create.js'

// 捕获未处理的异常
process.on('uncaughtException', (error) => {
	console.error(error.message)
	process.exit(1)
})

// 定义 CLI 程序
program
	.name('fpt')
	.description('快速创建项目')
	.version('1.0.0')

// 创建项目命令
program
	.command('create <project-name>')
	.description('创建一个新项目')
	.requiredOption('-t, --template <template>', '使用指定模板')
	.action(async (projectName, options) => {
		await createCommand(projectName, options.template)
	})

// 解析命令行参数
program.parse()
