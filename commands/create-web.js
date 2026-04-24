import inquirer from 'inquirer'

export async function createWeb() {
	console.log('正在创建项目的 web 端......')
	const loginOptions = await inquirer.prompt([
		{
			// 多选框
			type: 'checkbox',
			name: 'loginWay',
			message: '请选择登录方式: (↑/↓ 切换, 空格选择, a 全选, 回车确认)',
			// 选项
			choices: [
				{
					name: '用户名密码登录',
					value: 'account'
				},
				{
					name: '手机号登录',
					value: 'phone'
				},
				{
					name: '邮箱登录',
					value: 'email'
				}
			],
			// 默认选中某些项
			default: ['account']
		}
	])

	console.log(loginOptions)

	const githubActionsOptions = await inquirer.prompt([
		{
			type: 'confirm',
			name: 'useGithubActions',
			message: '是否使用 GitHub Actions 自动部署项目？',
			default: false
		}
	])

	console.log(githubActionsOptions)
	console.log('创建完成!')
}
