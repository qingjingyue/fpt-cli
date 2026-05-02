const map = {
	account: 'AccountLogin',
	phone: 'PhoneLogin',
	email: 'EmailLogin'
}

for (const [key, value] in map) {
	console.log(key + ':' + value)
}

for (const m in map) {
	console.log(m)
}

for (const [key, value] of Object.entries(map)) {
	console.log(key + ':' + value)
}

for (const m of Object.entries(map)) {
	console.log(m)
}
