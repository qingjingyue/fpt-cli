/**
 * 获取字符串的前缀 (假设字符串为"Abc-defHij",则前缀为"abc")
 * @param {string} str 字符串
 * @returns {string} 字符串的前缀
 */
export function getPrefix(str) {
	// 获取字符串的前缀 (假设字符串为"Abc-defHij",则前缀为"abc")
	if (!str) {
		return ''
	}
	// 第一个字符转换为小写
	str = str[0].toLowerCase() + str.slice(1)

	return str
		.replaceAll(/[ _]/g, '-')
		.replaceAll(/[A-Z]/g, (match) => '-' + match.toLowerCase())
		.split('-')[0]
		.toLowerCase()
}

/**
 * 获取字符串的大驼峰
 * @param {string} str 字符串
 * @returns {string} 字符串的大驼峰
 */
export function toPascalCase(str) {
	return str.replace(/(^\w|[-_\s]+\w)/g, (match) =>
		match.replace(/[-_\s]+/g, '').toUpperCase()
	)
}

/**
 * 获取字符串的下划线命名法
 * @param {string} str 字符串
 * @returns {string} 字符串的下划线命名法
 */
export function toSnake(str) {
	str = toPascalCase(str)
	return str
		.replace(/([A-Z])/g, '_$1')
		.replace(/^_/, '')
		.replace(
			/_([A-Z]+)(?=[A-Z][a-z]|$)/g,
			(match, p1) => '_' + p1.toLowerCase()
		)
		.toLowerCase()
}

export default {
	getPrefix,
	toPascalCase,
	toSnake
}
