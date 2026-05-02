import { Constants } from '../utils/constants.js'
import fs from 'fs-extra'

// 项目目录
const destPath = Constants.cmdDir

/**
 * 清空项目
 */
export function clear() {
	console.log(`正在清空项目 ${destPath}`)
	fs.emptyDirSync(destPath)
	console.log('清空完成')
}
