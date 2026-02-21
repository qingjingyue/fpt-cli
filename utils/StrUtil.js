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
}
