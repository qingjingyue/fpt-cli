import { Constants } from '../utils/constants.js'
import fs from 'fs-extra'
import path from 'path'
import { createGithubActions } from './create-githubactions.js'
import { createSkills } from './create-skills.js'

// 与GitHub Actions有关的内容
const deployFilePath = path.join('.github', 'workflows', 'deploy-web.yml')

// 定义需要渲染的文件
const renderFiles = [
	path.join('conf.d', 'default.conf'),
	path.join('env', '.env'),
	'docker-compose.yaml',
	'index.html',
	'package.json'
]

// 定义排除目录
const excludeDirs = ['.idea', '.vscode', 'node_modules', 'dist']
// 定义排除文件
const excludeFiles = ['auto-imports.d.ts', 'components.d.ts', ...renderFiles]

// 项目名称
const name = Constants.cmdDirName
// 模板目录
const templatePath = path.join(Constants.templateDir, 'vue')
// 目标目录
const destPath = path.join(Constants.cmdDir, Constants.cmdDirName + '-web')

/**
 * 创建项目的 web 端
 * @param {Object} options 选项对象
 * @param {Object} options.loginWay 登录方式
 * @param {boolean} options.loginWay.account
 * @param {boolean} options.loginWay.phone
 * @param {boolean} options.loginWay.email
 * @param {boolean} options.githubActions 是否使用 GitHub Actions 自动部署项目
 * @param {boolean} options.skills 是否生成 Claude Code 开发规范 skills
 */
export async function createWeb(options) {
	if (fs.pathExistsSync(destPath)) {
		console.log(`项目${Constants.cmdDirName}-web存在,取消创建`)
		return
	}
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

	// 处理GitHub Actions 相关文件
	if (options.githubActions) {
		createGithubActions(templatePath, destPath, name, deployFilePath)
	}

	// 处理 Claude Code skills
	if (options.skills) {
		createSkills(destPath, 'frontend-dev-standards')
	}

	console.log(`项目${Constants.cmdDirName}-web创建成功`)
}
