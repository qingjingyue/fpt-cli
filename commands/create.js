import fs from 'fs-extra'
import path from 'path'
import url from 'url'
import { createVueProject } from './create-vue.js'
import { createSpringBootProject } from './create-springboot.js'

// 模板名称
export const templateNames = ['vue', 'springboot']

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

	// 开始创建...
	switch (template) {
		case 'vue':
			createVueProject(name, templatePath, destPath)
			break
		case 'springboot':
			createSpringBootProject(name, templatePath, destPath)
			break
		default:
			break
	}

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
