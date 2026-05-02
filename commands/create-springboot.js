import fs from 'fs-extra'
import path from 'path'

// 定义排除目录和文件
const excludeDirs = ['.idea', 'logs', 'target']
const excludeFiles = ['docker-compose.yaml']
// 定义需要渲染的文件
const renderFiles = ['docker-compose.yaml']

/**
 * 构建模板创建SpringBoot项目
 * @param {string} cwdDir 当前目录
 * @param {string} cwdDirName 项目名称
 * @param {string} templateDir 模板目录路径
 */
export function createSpringBootProject(cwdDir, cwdDirName, templateDir) {
	const name = cwdDirName
	const templatePath = path.join(templateDir, 'springboot')
	const destPath = path.join(cwdDir, cwdDirName + '-server')

	// 递归,渲染模板文件
	renderTemplates(templatePath, destPath, { name })

	// 渲染指定文件
	renderFiles.forEach((file) => {
		const templateFilePath = path.join(templatePath, file)
		// 读取文件内容
		const content = fs.readFileSync(templateFilePath, 'utf-8')
		// 渲染文件内容
		const renderedContent = content.replaceAll('example', name)
		// 写入渲染结果到目标文件
		const destFilePath = path.join(destPath, file)
		fs.writeFileSync(destFilePath, renderedContent)
	})

	// 准备路径   .github/workflows/deploy-server.yml
	const WorkflowPath = path.join(
		templatePath,
		'..',
		'.github/workflows/deploy-server.yml'
	)
	const WorkflowDestPath = path.join(
		destPath,
		'..',
		'.github/workflows/deploy-server.yml'
	)
	// 替换workflow文件中的项目名称 ('example' -> name)
	const workflowContent = fs.readFileSync(WorkflowPath, 'utf-8')
	const replacedWorkflowContent = workflowContent.replaceAll('example', name)
	// 确保目标目录存在
	fs.ensureDirSync(path.dirname(WorkflowDestPath))
	// 写入渲染结果到文件
	fs.writeFileSync(WorkflowDestPath, replacedWorkflowContent)
}

/**
 * 递归渲染模板文件
 * @param {string} templatePath 模板目录路径
 * @param {string} destPath 目标目录路径
 * @param {object} data 渲染数据
 */
function renderTemplates(templatePath, destPath, data) {
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
				file = data.name
			}
			// 构建目标子目录路径
			const destSubDir = path.join(destPath, file)
			// 递归渲染子目录中的模板文件
			renderTemplates(templateFilePath, destSubDir, data)
		} else {
			// 检查是否为排除文件
			if (excludeFiles.includes(file)) {
				continue
			}
			// 读取模板文件内容
			const content = fs.readFileSync(templateFilePath, 'utf-8')
			// 渲染模板内容 ('example' -> data.name)
			const renderedResult = content.replaceAll('example', data.name)
			// 构建目标文件路径
			const destFilePath = path.join(destPath, file)
			// 写入渲染结果到文件
			fs.writeFileSync(destFilePath, renderedResult)
		}
	}
}
