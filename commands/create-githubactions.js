import path from 'path'
import fs from 'fs-extra'

/**
 * 创建GitHub Actions文件
 * @param {string} templatePath 模板文件夹路径
 * @param {string} destPath 目标文件夹路径
 * @param {string} name 项目名称
 * @param {string} deployFilePath 部署文件路径
 */
export function createGithubActions(
	templatePath,
	destPath,
	name,
	deployFilePath
) {
	// 处理workflow文件
	const WorkflowTemplatePath = path.join(templatePath, '..', deployFilePath)
	const WorkflowDestPath = path.join(destPath, '..', deployFilePath)
	// 读取workflow文件内容
	const workflowContent = fs.readFileSync(WorkflowTemplatePath, 'utf-8')
	// 替换项目名称 ('example' -> name)
	const replacedWorkflowContent = workflowContent.replaceAll('example', name)
	// 确保目标目录存在
	fs.ensureDirSync(path.dirname(WorkflowDestPath))
	// 写入渲染结果到文件
	fs.writeFileSync(WorkflowDestPath, replacedWorkflowContent)
}
