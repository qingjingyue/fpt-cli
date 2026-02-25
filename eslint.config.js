import js from '@eslint/js'
import globals from 'globals'

export default [
	// 1) 全局忽略：永远不需要检查的文件夹
	{
		ignores: ['**/dist/**', '**/build/**', '**/node_modules/**']
	},

	// 2) 继承 ESLint 官方推荐规则
	js.configs.recommended,

	// 3) 针对项目源代码的自定义配置
	{
		files: ['**/*.js'], // 只应用于 src 目录下的 .js 文件
		languageOptions: {
			ecmaVersion: 'latest', // 使用最新的 ECMAScript 语法
			sourceType: 'module', // 代码是 ES 模块 (使用 import/export)
			globals: {
				// ...globals.browser, // 浏览器环境全局变量 (如 window, document)
				...globals.node, // 如果是 Node.js 环境，取消这行注释
				MY_CUSTOM_GLOBAL: 'readonly' // 自定义只读全局变量
			}
		},
		rules: {
			// 在这里覆盖或添加规则
			'no-console': 'off', // 可以使用 console
			// semi: ['error', 'always'], // 强制使用分号，否则报错
			quotes: ['error', 'single'], // 强制使用单引号，否则报错
			'no-unused-vars': ['warn'] // 未使用的变量给出警告而非错误 (默认是 error)
		}
	}
]
