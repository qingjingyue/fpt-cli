import fs from 'fs-extra'
import ejs from 'ejs'
import path from 'path'
import url from 'url'
import { getPrefix, toPascalCase, toSnake } from '../utils/StrUtil.js'
import { templateNames, excludeDirs, excludeFiles } from '../utils/Constants.js'

// 获取当前文件所在目录
const dirname = path.dirname(url.fileURLToPath(import.meta.url))

/**
 * 创建项目
 * @param {string} name 项目名称
 * @param {string} template 选择的模板
 */
export async function createCommand(name, template) {
	// 参数校验
	validate({ name, template })
	// 获取模板目录
	const templatePath = path.join(dirname, '..', 'templates', template)
	// 获取目标目录		(process.cwd()为当前执行命令的目录)
	const destPath = path.join(process.cwd(), name)
	// 遍历模板目录,进行渲染
	await renderTemplates(templatePath, destPath, {
		projectName: name,
		projectNamePrefix: getPrefix(name),
		projectNamePascalCase: toPascalCase(name),
		projectNameSnakeCase: toSnake(name)
	})
	// 输出成功信息
	console.log(`项目${name}创建成功`)
}

/**
 * 校验参数
 * @param {object} params 参数对象
 * @param {string} params.name 项目名称
 * @param {string} params.template 选择的模板
 */
function validate({ name, template }) {
	// 校验项目名称
	if (!/^[a-zA-Z0-9_-]+$/.test(name)) {
		throw new Error('项目名称只能包含字母、数字、下划线和短横线')
	}
	// 校验模板名称
	isTemplateName(template)
	// 校验项目目录是否存在
	const destPath = path.join(process.cwd(), name)
	if (fs.existsSync(destPath)) {
		throw new Error(`目录${destPath}已存在,请选择其他项目名称`)
	}
}

/**
 * 校验模板名称
 * @param {string} template 模板名称
 */
function isTemplateName(template) {
	if (!templateNames.includes(template)) {
		throw new Error(`模板${template}不存在`)
	}
}

/**
 * 遍历目标目录中的模板文件,进行渲染
 * @param {string} destPath 目标目录路径
 * @param {object} data 渲染数据
 */
async function renderTemplates(templatePath, destPath, data) {
	//创建目标目录
	await fs.mkdir(destPath)
	// 读取模板目录中的所有文件
	const files = await fs.readdir(templatePath)
	for (let file of files) {
		// 构建文件路径
		const templateFilePath = path.join(templatePath, file)
		// 检查是否为目录
		if (fs.statSync(templateFilePath).isDirectory()) {
			// 检查是否为排除目录
			if (excludeDirs.includes(file)) {
				continue
			}
			// 检查是否为需要重命名的目录
			if (file === 'example') {
				file = data.projectNamePrefix
			}
			// 创建目标子目录
			const destSubDir = path.join(destPath, file)
			// 递归渲染子目录中的模板文件
			await renderTemplates(templateFilePath, destSubDir, data)
		} else if (templateFilePath.endsWith('.ejs')) {
			// 渲染模板文件
			const renderedResult = await ejs.renderFile(templateFilePath, data)
			// 构建目标文件路径
			file = file.replace('.ejs', '')
			if (file === 'SpringBootTemplateApplication.java') {
				file = data.projectNamePascalCase + 'Application.java'
			}
			const destFilePath = path.join(destPath, file)
			// 写入渲染结果到目标文件
			await fs.writeFile(destFilePath, renderedResult)
		} else if (templateFilePath.includes('springboot')) {
			// 检查是否为排除文件
			if (excludeFiles.includes(file)) {
				continue
			}
			// 将springboot模板中的"example"替换为项目名前缀
			const content = fs.readFileSync(templateFilePath, 'utf8')
			const result = content.replace(/example/g, data.projectNamePrefix)
			// 写入渲染结果到目标文件
			const destFilePath = path.join(destPath, file)
			await fs.writeFile(destFilePath, result)
		} else {
			// 检查是否为排除文件
			if (excludeFiles.includes(file)) {
				continue
			}
			// 复制非模板文件到目标目录
			const destFilePath = path.join(destPath, file)
			await fs.copy(templateFilePath, destFilePath)
		}
	}
}
