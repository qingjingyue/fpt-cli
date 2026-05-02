#!/usr/bin/env node

import { program } from 'commander'
import { create } from '../commands/create.js'
import { clear } from '../commands/clear.js'

// 捕获未处理的异常
process.on('uncaughtException', (error) => {
	console.error(error.message)
	process.exit(1)
})

// 定义 CLI 程序
program.name('fpt').description('快速创建项目').version('1.0.0')

// 创建项目命令
program
	.command('create')
	.description('创建一个项目')
	.option(
		'--default',
		'创建项目的 web,server 端,登录方式为 account,不使用 GitHub Actions 自动部署项目'
	)
	.option('-e, --endpoint <endpoint>', '要创建的端 {web,server}')
	.option('--login-way <loginWay>', '登录方式 {account,phone,email}')
	.option('--github-actions', '使用 GitHub Actions 自动部署项目')
	.action(async (options) => await create(options))

// 清理目录命令
program.command('clear').description('清空项目').action(clear)

// 解析命令行参数
program.parse()
