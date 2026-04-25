import { Constants } from '../utils/constants.js'
import fs from 'fs-extra'
import path from 'path'

// 与登录方式有关的内容
const loginWayMap = {
	account: path.join('src', 'views', 'login', 'AccountLogin.vue'),
	phone: path.join('src', 'views', 'login', 'PhoneLogin.vue'),
	email: path.join('src', 'views', 'login', 'EmailLogin.vue')
}
const loginLayoutPath = path.join('src', 'views', 'login', 'LoginLayout.vue')

// 与GitHub Actions有关的内容
const githubActionslist = [
	path.join('conf.d', 'default.conf'),
	'docker-compose.yaml'
]
const deployFilePath = path.join('.github', 'workflows', 'deploy-web.yml')

// 定义需要渲染的文件
const renderFiles = [path.join('env', '.env'), 'index.html', 'package.json']

// 定义排除目录和文件
const excludeDirs = ['.vscode', 'node_modules', 'dist', 'conf.d']
const excludeFiles = [
	'auto-imports.d.ts',
	'components.d.ts',
	...Object.values(loginWayMap),
	...githubActionslist,
	...renderFiles
]

// 项目名称
const name = Constants.cmdDirName
// 模板目录
const templatePath = path.join(Constants.templateDir, 'vue')
// 目标目录
const destPath = path.join(Constants.cmdDir, Constants.cmdDirName + '-web')

/**
 * 创建项目的 web 端
 * @param {Object} options 选项对象
 * @param {('account' | 'phone' | 'email')[]} options.loginWay 登录方式
 * @param {boolean} options.githubActions 是否使用 GitHub Actions 自动部署项目
 */
export async function createWeb(options) {
	console.log('正在创建项目的 web 端......')

	// 复制模板目录到目标目录
	fs.copySync(templatePath, destPath, {
		filter: (src) => {
			// 过滤目录
			const isExcludeDir = excludeDirs.some((dir) => src.endsWith(dir))
			// 过滤文件
			const isExcludeFile = excludeFiles.some((file) =>
				src.endsWith(file)
			)
			return !isExcludeDir && !isExcludeFile
		}
	})

	// 渲染指定文件 ('example' -> name)
	renderFiles.forEach((file) => {
		const templateFilePath = path.join(templatePath, file)
		const destFilePath = path.join(destPath, file)
		// 读取文件内容
		const content = fs.readFileSync(templateFilePath, 'utf-8')
		// 渲染文件内容
		const renderedContent = content.replaceAll('example', name)
		// 写入渲染结果到文件
		fs.writeFileSync(destFilePath, renderedContent)
	})

	// 处理登录方式相关文件
	for (const loginWay of options.loginWay) {
		// 模板登录方式文件路径
		const templateLoginWayPath = path.join(
			templatePath,
			loginWayMap[loginWay]
		)
		// 目标登录方式文件路径
		const destLoginWayPath = path.join(destPath, loginWayMap[loginWay])
		fs.copyFileSync(templateLoginWayPath, destLoginWayPath)
	}
	// 处理登录布局文件
	if (options.loginWay.length !== Object.keys(loginWayMap).length) {
		const destLoginLayoutPath = path.join(destPath, loginLayoutPath)
		let content = fs.readFileSync(destLoginLayoutPath, 'utf-8')
		const loginWays = Object.keys(loginWayMap).filter(
			(item) => !options.loginWay.includes(item)
		)
		const map = {
			account: 'AccountLogin',
			phone: 'PhoneLogin',
			email: 'EmailLogin'
		}
		for (const loginWay of loginWays) {
			content = content
				.replaceAll(`<${map[loginWay]} />`, '')
				.replaceAll(
					`import ${map[loginWay]} from '@/views/login/${map[loginWay]}.vue'`,
					''
				)
		}

		fs.writeFileSync(destLoginLayoutPath, content)
	}

	// 处理GitHub Actions 相关文件
	if (options.githubActions) {
		for (const file of githubActionslist) {
			const templateFilePath = path.join(templatePath, file)
			const destFilePath = path.join(destPath, file)
			// 读取模板文件内容
			const templateContent = fs.readFileSync(templateFilePath, 'utf-8')
			// 替换项目名称 ('example' -> name)
			const renderedContent = templateContent.replaceAll('example', name)
			// 确保目标目录存在
			fs.ensureDirSync(path.dirname(destFilePath))
			// 写入渲染结果到文件
			fs.writeFileSync(destFilePath, renderedContent)
		}
		// 处理workflow文件
		const WorkflowTemplatePath = path.join(
			templatePath,
			'..',
			deployFilePath
		)
		const WorkflowDestPath = path.join(destPath, '..', deployFilePath)
		// 读取workflow文件内容
		const workflowContent = fs.readFileSync(WorkflowTemplatePath, 'utf-8')
		// 替换项目名称 ('example' -> name)
		const replacedWorkflowContent = workflowContent.replaceAll(
			'example',
			name
		)
		// 确保目标目录存在
		fs.ensureDirSync(path.dirname(WorkflowDestPath))
		// 写入渲染结果到文件
		fs.writeFileSync(WorkflowDestPath, replacedWorkflowContent)
	}

	console.log(`项目${Constants.cmdDirName}-web创建成功`)
}
