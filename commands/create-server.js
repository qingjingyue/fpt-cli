import { Constants } from '../utils/constants.js'
import fs from 'fs-extra'
import path from 'path'
import { createGithubActions } from './create-githubactions.js'

// 与登录方式有关的内容
const loginWayMap = {
	application: path.join(
		'server',
		'src',
		'main',
		'resources',
		'application.yaml'
	)
}

// 与GitHub Actions有关的内容
const githubActionsList = ['docker-compose.yaml']
const deployFilePath = path.join('.github', 'workflows', 'deploy-server.yml')

// 定义需要渲染的文件
const renderFiles = []

// 定义排除目录(写目录名即可)
const excludeDirs = ['.idea', 'logs', 'target', 'test']
// 定义排除文件
const excludeFiles = [...githubActionsList, ...renderFiles]

// 项目名称
const name = Constants.cmdDirName
// 模板目录
const templatePath = path.join(Constants.templateDir, 'springboot')
// 目标目录
const destPath = path.join(Constants.cmdDir, Constants.cmdDirName + '-server')

/**
 * 创建项目的 server 端
 * @param {Object} options 选项对象
 * @param {Object} options.loginWay 登录方式
 * @param {boolean} options.loginWay.account
 * @param {boolean} options.loginWay.phone
 * @param {boolean} options.loginWay.email
 * @param {boolean} options.githubActions 是否使用 GitHub Actions 自动部署项目
 */
export async function createServer(options) {
	if (fs.pathExistsSync(destPath)) {
		console.log(`项目${Constants.cmdDirName}-server存在,取消创建`)
		return
	}
	console.log('正在创建项目的 server 端......')

	// 递归,渲染模板文件
	renderTemplates(templatePath, destPath)

	// 处理登录方式相关文件
	if (Object.values(options.loginWay).includes(false)) {
		const destAppPath = path.join(destPath, loginWayMap.application)
		let content = fs.readFileSync(destAppPath, 'utf-8')
		for (const [key, value] of Object.entries(options.loginWay)) {
			if (value) continue
			content = content.replaceAll(`${key}: true`, `${key}: false`)
		}
		fs.writeFileSync(destAppPath, content)
	}

	// 处理GitHub Actions 相关文件
	if (options.githubActions) {
		createGithubActions(
			githubActionsList,
			templatePath,
			destPath,
			name,
			deployFilePath
		)
	}

	console.log(`项目${Constants.cmdDirName}-server创建成功`)
}

/**
 * 递归渲染模板文件
 * @param {string} templatePath 模板目录路径
 * @param {string} destPath 目标目录路径
 */
function renderTemplates(templatePath, destPath) {
	//创建目标目录
	fs.mkdirSync(destPath)
	// 读取模板目录中的所有文件
	const files = fs.readdirSync(templatePath)
	for (let file of files) {
		// 构建模板文件路径
		const templateFilePath = path.join(templatePath, file)
		// 检查是否为目录
		if (fs.statSync(templateFilePath).isDirectory()) {
			// 检查是否为排除目录
			if (excludeDirs.includes(file)) {
				continue
			}
			// 检查是否为需要重命名的目录
			if (file === 'example') {
				file = name
			}
			// 构建目标子目录路径
			const destSubDir = path.join(destPath, file)
			// 递归渲染子目录中的模板文件
			renderTemplates(templateFilePath, destSubDir)
		} else {
			// 检查是否为排除文件
			if (excludeFiles.includes(file)) {
				continue
			}
			// 读取模板文件内容
			const content = fs.readFileSync(templateFilePath, 'utf-8')
			// 渲染模板内容 ('example' -> data.name)
			const renderedResult = content.replaceAll('example', name)
			// 构建目标文件路径
			const destFilePath = path.join(destPath, file)
			// 写入渲染结果到文件
			fs.writeFileSync(destFilePath, renderedResult)
		}
	}
}
