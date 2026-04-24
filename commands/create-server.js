import { Constants } from '../utils/constants.js'

/**
 * 创建项目的 server 端
 * @param {('account' | 'phone' | 'email')[]} loginWay 登录方式
 * @param {boolean} useGithubActions 是否使用 GitHub Actions 自动部署项目
 */
export async function createServer(loginWay, useGithubActions) {
	console.log('正在创建项目的 server 端......')

	console.log(`项目${Constants.cwdDirName}-server创建成功`)
}
