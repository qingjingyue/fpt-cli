import { Constants } from '../utils/constants.js'
import fs from 'fs-extra'
import path from 'path'

/**
 * 创建 Claude Code skills 文件
 * @param {string} destPath 目标项目目录
 * @param {string} skillName skills 名称（对应 templates/skills/ 下的目录名）
 */
export function createSkills(destPath, skillName) {
	const skillsTemplateDir = path.join(
		Constants.templateDir,
		'skills',
		skillName
	)
	const skillsDestDir = path.join(
		destPath,
		'.claude',
		'skills',
		skillName
	)
	fs.ensureDirSync(skillsDestDir)

	const skillFile = 'SKILL.md'
	const skillContent = fs.readFileSync(
		path.join(skillsTemplateDir, skillFile),
		'utf-8'
	)
	fs.writeFileSync(
		path.join(skillsDestDir, skillFile),
		skillContent
	)
}
