import path from 'path'
import url from 'url'

export class Constants {
	// 模板目录
	static templateDir = path.join(
		path.dirname(url.fileURLToPath(import.meta.url)),
		'..',
		'templates'
	)

	// 命令执行完整目录
	static cmdDir = process.cwd()
	// 命令执行目录名
	static cmdDirName = path.basename(Constants.cmdDir)
}
