import inquirer from 'inquirer'
import { Constants } from '../utils/constants.js'
import { createWeb } from './create-web.js'
import { createServer } from './create-server.js'

/**
 * 创建项目
 * @param {object} options 命令选项
 * @param {boolean} options.default 是否使用默认选项
 * @param {('web' | 'server' )[]} options.endpoint 要创建的端
 * @param {('account' | 'phone' | 'email')[]} options.loginWay 登录方式
 * @param {boolean} options.githubActions 是否使用 GitHub Actions 自动部署项目
 */
export async function create(options) {
	// console.log(options)

	// 效验项目目录名称
	// if (!/^[a-z]+$/.test(Constants.cwdDirName)) {
	// 	throw new Error('项目目录名称只能包含小写字母')
	// }

	// 处理默认情况
	if (options.default) {
		options.endpoint = 'all'
		options.loginWay = ['account']
		options.githubActions = false
	}

	// 交互式选项

	// 处理端
	if (options.endpoint == null) {
		const endpointOptions = await inquirer.prompt([
			{
				type: 'checkbox',
				name: 'endpoint',
				message:
					'请选择要创建的端: (↑/↓ 切换, 空格选择, a 全选, 回车确认)',
				choices: [
					{
						name: '网页端',
						value: 'web'
					},
					{
						name: '服务端',
						value: 'server'
					}
				],
				default: ['web']
			}
		])
		options.endpoint = endpointOptions.endpoint
		if (options.endpoint.length == 0) {
			options.endpoint = 'web'
		} else if (options.endpoint.length == 2) {
			options.endpoint = 'all'
		}
	}

	// 处理登录方式
	if (options.loginWay == null) {
		const loginOptions = await inquirer.prompt([
			{
				// 多选框
				type: 'checkbox',
				name: 'loginWay',
				message:
					'请选择登录方式: (↑/↓ 切换, 空格选择, a 全选, 回车确认)',
				// 选项
				choices: [
					{
						name: '用户名密码登录',
						value: 'account'
					},
					{
						name: '手机号登录',
						value: 'phone'
					},
					{
						name: '邮箱登录',
						value: 'email'
					}
				],
				// 默认选中某些项
				default: ['account']
			}
		])
		options.loginWay = loginOptions.loginWay
		if (options.loginWay.length == 0) {
			options.loginWay = ['account']
		}
	}

	// 处理是否使用 GitHub Actions 自动部署项目
	if (options.githubActions == null) {
		const githubActionsOptions = await inquirer.prompt([
			{
				type: 'confirm',
				name: 'useGithubActions',
				message: '是否使用 GitHub Actions 自动部署项目？',
				default: false
			}
		])
		options.githubActions = githubActionsOptions.useGithubActions
	}

	// 解析选项
	switch (options.endpoint) {
		case 'web':
			createWeb(options)
			break
		case 'server':
			createServer(options)
			break
		case 'all':
			createWeb(options)
			createServer(options)
			break
		default:
			throw new Error(`不支持的端: ${options.endpoint}`)
	}
}
