import inquirer from 'inquirer'
import { createVueProject } from './create-vue.js'
import { createSpringBootProject } from './create-springboot.js'
import { Constants } from '../utils/constants.js'

/**
 * 创建项目
 * @param {*} options 命令选项
 */
export async function create(options) {
	// console.log(options)

	// 效验项目目录名称
	if (!/^[a-z]+$/.test(Constants.cwdDirName)) {
		throw new Error('项目目录名称只能包含小写字母')
	}

	// 交互式选项

	// 处理端
	let endpoint = null
	if (options.endpoint == null) {
		const endpointOptions = await inquirer.prompt([
			{
				type: 'checkbox',
				name: 'endpoint',
				message: '请选择要创建的端:',
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
		endpoint = endpointOptions.endpoint
		if (endpoint.length == 0) {
			endpoint = 'web'
		} else if (endpoint.length == 2) {
			endpoint = 'all'
		}
	}
	endpoint = options.endpoint || endpoint

	// 处理登录方式
	let loginWay = null
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
		loginWay = loginOptions.loginWay
		if (loginWay.length == 0) {
			loginWay = ['account']
		}
	}
	loginWay = options.loginWay || loginWay

	// 处理是否使用 GitHub Actions 自动部署项目
	let useGithubActions = null
	if (options.githubActions == null) {
		const githubActionsOptions = await inquirer.prompt([
			{
				type: 'confirm',
				name: 'useGithubActions',
				message: '是否使用 GitHub Actions 自动部署项目？',
				default: false
			}
		])
		useGithubActions = githubActionsOptions.useGithubActions
	}
	useGithubActions = options.githubActions || useGithubActions

	// 解析选项
	switch (endpoint) {
		case 'web':
			// createVueProject(loginWay, useGithubActions)
			break
		case 'server':
			// createSpringBootProject(loginWay, useGithubActions)
			break
		case 'all':
			// createVueProject(loginWay, useGithubActions)
			// createSpringBootProject(loginWay, useGithubActions)
			break
		default:
			throw new Error(`不支持的端: ${options.endpoint}`)
	}
}
