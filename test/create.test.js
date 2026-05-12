import { describe, it, before, after, beforeEach } from 'node:test'
import assert from 'node:assert'
import fs from 'fs-extra'
import path from 'node:path'
import os from 'node:os'

const originalCwd = process.cwd()
let tmpDir
let projectDir

/// 执行时机: 在所有测试用例之前执行
before(() => {
	tmpDir = fs.mkdtempSync(path.join(os.tmpdir(), 'fpt-cli-test-'))
})

/// 执行时机: 在所有测试用例之后执行
after(() => {
	process.chdir(originalCwd)
	fs.removeSync(tmpDir)
})

/// 执行时机: 在每个测试用例之前执行
beforeEach(() => {
	process.chdir(tmpDir)
	projectDir = path.join(tmpDir, 'testproj')
	fs.removeSync(projectDir)
	fs.mkdirSync(projectDir)
	process.chdir(projectDir)
})

describe('createWeb', () => {
	it('创建 web 项目并渲染 example → 项目名', async () => {
		const { createWeb } = await import('../commands/create-web.js')
		await createWeb({
			loginWay: { account: true, phone: false, email: false },
			githubActions: false,
			skills: false
		})

		const webDir = path.join(projectDir, 'testproj-web')
		assert.ok(fs.existsSync(webDir), 'web 项目目录存在')

		const pkgContent = fs.readFileSync(
			path.join(webDir, 'package.json'),
			'utf-8'
		)
		assert.ok(pkgContent.includes('"name": "testproj"'), 'package.json 已渲染')

		const indexContent = fs.readFileSync(
			path.join(webDir, 'index.html'),
			'utf-8'
		)
		assert.ok(indexContent.includes('testproj'), 'index.html 已渲染')

		assert.ok(fs.existsSync(path.join(webDir, 'src', 'main.ts')), 'main.ts')
		assert.ok(fs.existsSync(path.join(webDir, 'src', 'router')), 'router')
		assert.ok(fs.existsSync(path.join(webDir, 'src', 'apis')), 'apis')
		assert.ok(fs.existsSync(path.join(webDir, 'env', '.env')), '.env')

		assert.ok(!fs.existsSync(path.join(webDir, 'node_modules')), '排除 node_modules')
		assert.ok(!fs.existsSync(path.join(webDir, '.vscode')), '排除 .vscode')
	})

	it('--skills 生成前端开发规范文件', async () => {
		const { createWeb } = await import('../commands/create-web.js')
		await createWeb({
			skills: true,
			loginWay: { account: true, phone: false, email: false },
			githubActions: false
		})

		const skillPath = path.join(
			projectDir,
			'testproj-web',
			'.claude',
			'skills',
			'frontend-dev-standards',
			'SKILL.md'
		)
		assert.ok(fs.existsSync(skillPath), '前端 skills 文件已生成')
		const content = fs.readFileSync(skillPath, 'utf-8')
		assert.ok(content.includes('Vue 3 + TypeScript'), 'skills 内容正确')
	})

	it('重复创建同名项目时跳过', async () => {
		const { createWeb } = await import('../commands/create-web.js')
		// 第一次创建
		await createWeb({
			skills: false,
			loginWay: { account: true, phone: false, email: false },
			githubActions: false
		})
		// 第二次创建同名项目 — 应跳过不抛异常
		await createWeb({
			skills: false,
			loginWay: { account: true, phone: false, email: false },
			githubActions: false
		})
	})
})

describe('createServer', () => {
	it('创建 server 项目（含 skills）并渲染 example', async () => {
		const { createServer } = await import('../commands/create-server.js')
		await createServer({
			skills: true,
			loginWay: { account: true, phone: false, email: false },
			githubActions: false
		})

		const serverDir = path.join(projectDir, 'testproj-server')
		assert.ok(fs.existsSync(serverDir), 'server 项目目录存在')

		// Java 包路径渲染
		const appJava = path.join(
			serverDir,
			'server', 'src', 'main', 'java', 'com',
			'testproj', 'server', 'Application.java'
		)
		assert.ok(fs.existsSync(appJava), 'Java 包路径中 example 已替换')

		// pom.xml 渲染
		const pomContent = fs.readFileSync(
			path.join(serverDir, 'pom.xml'),
			'utf-8'
		)
		assert.ok(pomContent.includes('testproj'), 'pom.xml 已渲染')

		// 排除项
		assert.ok(!fs.existsSync(path.join(serverDir, '.idea')), '排除 .idea')

		// skills 文件
		const skillPath = path.join(
			serverDir,
			'.claude', 'skills', 'backend-dev-standards', 'SKILL.md'
		)
		assert.ok(fs.existsSync(skillPath), '后端 skills 文件已生成')
		const skillContent = fs.readFileSync(skillPath, 'utf-8')
		assert.ok(skillContent.includes('Spring Boot'), 'skills 内容正确')

		// 登录方式配置
		const appYaml = fs.readFileSync(
			path.join(
				serverDir,
				'server', 'src', 'main', 'resources', 'application.yaml'
			),
			'utf-8'
		)
		assert.ok(appYaml.includes('account: true'), 'account 已启用')
		assert.ok(appYaml.includes('phone: false'), 'phone 已禁用')
		assert.ok(appYaml.includes('email: false'), 'email 已禁用')
	})

	it('全部登录方式启用时不修改 application.yaml', async () => {
		const { createServer } = await import('../commands/create-server.js')
		await createServer({
			skills: false,
			loginWay: { account: true, phone: true, email: true },
			githubActions: false
		})

		const appYaml = fs.readFileSync(
			path.join(
				projectDir,
				'testproj-server',
				'server', 'src', 'main', 'resources', 'application.yaml'
			),
			'utf-8'
		)
		assert.ok(appYaml.includes('account: true'), 'account 保持 true')
		assert.ok(appYaml.includes('phone: true'), 'phone 保持 true')
		assert.ok(appYaml.includes('email: true'), 'email 保持 true')
	})
})

describe('create', () => {
	it('--default 一键创建 web + server', async () => {
		const { create } = await import('../commands/create.js')
		await create({ default: true })

		const webDir = path.join(projectDir, 'testproj-web')
		const serverDir = path.join(projectDir, 'testproj-server')
		assert.ok(fs.existsSync(webDir), 'web 项目目录存在')
		assert.ok(fs.existsSync(serverDir), 'server 项目目录存在')
	})

	it('项目目录名校验规则', () => {
		// create.js 中校验: /^[a-z]+$/
		const validName = /^[a-z]+$/
		assert.ok(validName.test('myapp'), '纯小写字母通过')
		assert.ok(!validName.test('Bad-Name'), '拒绝大写字母和连字符')
		assert.ok(!validName.test('bad123'), '拒绝数字')
		assert.ok(!validName.test('bad_name'), '拒绝下划线')
		assert.ok(!validName.test(''), '拒绝空字符串')
	})
})

describe('createSkills', () => {
	it('正确复制 skills 模板到目标目录', async () => {
		const { createSkills } = await import('../commands/create-skills.js')
		const dest = fs.mkdtempSync(path.join(os.tmpdir(), 'skills-test-'))

		try {
			createSkills(dest, 'frontend-dev-standards')
			const skillPath = path.join(
				dest,
				'.claude', 'skills', 'frontend-dev-standards', 'SKILL.md'
			)
			assert.ok(fs.existsSync(skillPath), 'skills 文件已复制')
		} finally {
			fs.removeSync(dest)
		}
	})
})
