import { Constants } from '../utils/constants.js'

/**
 * 创建项目的 web 端
 * @param {Object} options 选项对象
 * @param {('account' | 'phone' | 'email')[]} options.loginWay 登录方式
 * @param {boolean} options.githubActions 是否使用 GitHub Actions 自动部署项目
 */
export async function createWeb(options) {
	console.log('正在创建项目的 web 端......')

	console.log(`项目${Constants.cwdDirName}-web创建成功`)
}
