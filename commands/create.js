import path from 'path'
import url from 'url'
import { createVueProject } from './create-vue.js'
import { createSpringBootProject } from './create-springboot.js'

// 端名称
export const endpointNames = ['web', 'server']

// 获取模板目录
const templateDir = path.join(path.dirname(url.fileURLToPath(import.meta.url)), '..', 'templates')

/**
 * 创建项目
 * @param {string} endpoint 选择的端
 */
export function createCommand(endpoint) {
	// 参数校验
	if (!endpointNames.includes(endpoint)) {
		throw new Error(`${endpoint}端不存在`)
	}
	// 获取命令执行目录名
	const cwdDir = process.cwd()
	const cwdDirName = path.basename(cwdDir)
	// 效验项目目录名称
	if (!/^[a-z]+$/.test(cwdDirName)) {
		throw new Error('项目目录名称只能包含小写字母')
	}
	// 创建项目
	switch (endpoint) {
		case 'web':
			createVueProject(cwdDir, cwdDirName, templateDir)
			break
		case 'server':
			createSpringBootProject(cwdDir, cwdDirName, templateDir)
			break
		default:
			break
	}

	// 输出项目创建成功信息
	console.log(`项目${cwdDirName}-${endpoint}创建成功`)
}
