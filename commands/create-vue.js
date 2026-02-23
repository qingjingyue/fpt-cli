import fs from 'fs-extra'
import path from 'path'
import { getPrefix } from '../utils/StrUtil.js'

// 定义排除目录和文件
const excludeDirs = ['node_modules', 'dist']
const excludeFiles = ['auto-imports.d.ts', 'components.d.ts']
// 定义需要渲染的文件
const renderFiles = [
	'index.html',
	'package.json',
	'.env.dev',
	'.env.prod',
	'docker-compose.yaml',
	'conf.d/default.conf'
]

/**
 * 构建模板创建Vue项目
 * @param {string} name 项目名称
 * @param {string} templatePath 模板目录路径
 * @param {string} destPath 目标目录路径
 */
export function createVueProject(name, templatePath, destPath) {
	// 复制模板目录到目标目录
	fs.copySync(templatePath, destPath, {
		// 过滤排除目录和文件
		filter: (src, dest) => {
			const isExcludeDir = excludeDirs.some((dir) => src.includes(dir))
			const isExcludeFile = excludeFiles.some((file) => src.includes(file))
			return !isExcludeDir && !isExcludeFile
		}
	})
	// 渲染指定文件 ('example' -> name)
	renderFiles.forEach((file) => {
		const filePath = path.join(destPath, file)
		// 读取文件内容
		const content = fs.readFileSync(filePath, 'utf-8')
		// 渲染文件内容
		const renderedContent = content
			.replaceAll('example-prefix', getPrefix(name))
			.replaceAll('example', name)
		// 写入渲染结果到文件
		fs.writeFileSync(filePath, renderedContent)
	})
	// 准备路径   .github/workflows/deploy-nginx.yml
	const WorkflowPath = path.join(templatePath, '..', '.github/workflows/deploy-nginx.yml')
	const WorkflowDestPath = path.join(destPath, '..', '.github/workflows/deploy-nginx.yml')
	// 替换workflow文件中的项目名称 ('example' -> name)
	const workflowContent = fs.readFileSync(WorkflowPath, 'utf-8')
	const replacedWorkflowContent = workflowContent.replaceAll('example', name)
	// 确保目标目录存在
	fs.ensureDirSync(path.dirname(WorkflowDestPath))
	// 写入渲染结果到文件
	fs.writeFileSync(WorkflowDestPath, replacedWorkflowContent)
}
