import { getPrefix, toPascalCase, toSnake } from '../utils/StrUtil.js'

const s = 'Abc-defHij nji'
console.log('前缀' + getPrefix(s))
console.log('大驼峰' + toPascalCase(s))
console.log('下划线' + toSnake(s))
