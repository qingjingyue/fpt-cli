#!/usr/bin/env node

import { program } from 'commander'
import { create } from '../commands/create.js'
import { createWeb } from '../commands/create-web.js'
import { createServer } from '../commands/create-server.js'

// 捕获未处理的异常
process.on('uncaughtException', (error) => {
	console.error(error.message)
	process.exit(1)
})

// 定义 CLI 程序
program.name('fpt').description('快速创建项目').version('1.0.0')

// 创建项目命令
const createProgram = program
	.command('create')
	.description('创建一个项目')
	.option('-e, --endpoint <endpoint>', '要创建的端 {web,server}')
	.option('--login-way <loginWay>', '登录方式 {account,phone,email}')
	.option('--github-actions', '使用 GitHub Actions 自动部署项目')
	.action(create)

// createProgram.command('web').description('创建项目的 web 端').action(createWeb)

// createProgram
// 	.command('server')
// 	.description('创建项目的 server 端')
// 	.action(createServer)

// 解析命令行参数
program.parse()
