import path from 'path'
import url from 'url'
import inquirer from 'inquirer'
import { createVueProject } from './create-vue.js'
import { createSpringBootProject } from './create-springboot.js'
import { log } from 'console'

// 获取模板目录
const templateDir = path.join(
	path.dirname(url.fileURLToPath(import.meta.url)),
	'..',
	'templates'
)

/**
 * 创建项目
 * @param {*} options 选项
 */
export async function create(options) {
	// 获取命令执行目录名
	const cwdDir = process.cwd()
	const cwdDirName = path.basename(cwdDir)
	// 效验项目目录名称
	// if (!/^[a-z]+$/.test(cwdDirName)) {
	// 	throw new Error('项目目录名称只能包含小写字母')
	// }

	console.log(options)

	// 解析选项
	// if(options.endpoint!=null) {
	// 	switch(options.endpoint) {
	// 		case 'web':
	// 			createVueProject()
	// 			break
	// 		case 'server':
	// 			createSpringBootProject()
	// 			break
	// 	}
	// }

	// 创建项目
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
			]
			// default: 'web'
		}
	])
	const endpoint = endpointOptions.endpoint

	// 输出项目创建成功信息
	console.log(`项目${cwdDirName}-${endpoint}创建成功`)
}
