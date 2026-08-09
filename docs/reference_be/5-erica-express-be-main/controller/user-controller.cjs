const {
	checkUserModel,
	checkUserIdModel,
	checkUserEmailModel,
	checkUserNicknameModel,
	addUserModel,
	logInUserModel,
	updateUserProfileModel,
	updateUserPasswordModel,
	deleteUserModel,
	addUserImageModel,
	getUserWriteCount
} = require('../model/users.cjs')

// const getUsers = (req, res) => {
// 	//TODO: 서버로 띄울 시 활셩화 필요
// 	// users.forEach((user) => {
// 	//   user.profile_image = user.profile_image.replace(
// 	//     "http://localhost:8000",
// 	//     `https://${req.headers.host}`
// 	//   );
// 	// });
// 	return res.status(200).json({ status: 200, message: null, data: users })
// }

const getUser = async (req, res) => {
	const userId = req.session.user.user_id

	if (!userId) return res.status(400).json({ status: 404, message: 'invalid_user_id', data: null })

	const user = await checkUserModel(userId)
	if (!user) return res.status(404).json({ status: 404, message: 'not_fount_user', data: null })

	//TODO: 서버로 띄울 시 활셩화 필요
	// user.profile_image = user.profile_image.replace(
	//   "http://localhost:8000",
	//   `https://${req.headers.host}`
	// );

	return res.status(200).json({ status: 200, message: null, data: user })
}

const addUser = async (req, res) => {
	const { email, nickname, password, profile_image } = req.body

	let profile_server_url = ''

	if (!email || !nickname || !password || !profile_image) {
		return res.status(400).json({ status: 404, message: 'invalid_input', data: null })
	}

	if (profile_image) {
		profile_server_url = addUserImageModel(profile_image)
	}

	const newUserId = await addUserModel({
		email,
		nickname,
		password,
		profile_image: profile_server_url
	})

	if (!newUserId || newUserId === -1)
		return res.status(500).json({ status: 500, message: 'internal_server_error', data: null })

	return res.status(201).json({
		status: 201,
		message: 'register_success',
		data: { userId: newUserId, profile_image }
	})
}

const logInUser = async (req, res) => {
	const { email, password } = req.body
	if (!email) return res.status(400).json({ status: 400, message: 'required_email', data: null })

	const user = await logInUserModel(email, password)
	if (!user) return res.status(404).json({ status: 404, message: 'invalid_email_or_password', data: null })
	if (user === -1) return res.status(500).json({ status: 500, message: 'internal_server_error', data: null })

	req.session.user = user

	//req.session -> {user: {userId: 1, email: "", }}
	return res.status(200).json({ status: 200, message: 'login_success', data: user })
}

//유저 프로필 변경
const updateUserProfile = async (req, res) => {
	const userId = Number(req.user_id)
	const { nickname, profile_image } = req.body

	let user_server_url = ''

	if (!userId) return res.status(400).json({ status: 400, message: 'invalid_user_id', data: null })

	if (!profile_image.includes(req.headers.host)) {
		const saved_image_url = addUserImageModel(profile_image)

		if (!saved_image_url) return res.status(500).json({ status: 500, message: 'internal_server_error', data: null })
		user_server_url = saved_image_url
		console.log(user_server_url)
	} else {
		// TODO: 배포할때 주소 변경해야함
		user_server_url = profile_image.replace(`http://${req.headers.host}`, 'http://localhost:8000')
		console.log(user_server_url)
	}
	const check = await checkUserIdModel(userId)

	if (check) return res.status(404).json({ status: 404, message: 'not_found_user', data: null })
	if (!check) return res.status(500).json({ status: 500, message: 'internal_server_error', data: null })

	if (!profile_image) return res.status(500).json({ status: 500, message: 'internal_server_error', data: null })

	const success = await updateUserProfileModel({
		userId,
		nickname,
		profile_image: user_server_url
	})

	if (success === -1) return res.status(500).json({ status: 500, message: 'internal_server_error', data: null })

	return res.status(201).json({ status: 201, message: 'update_user_data_success', data: null })
}

//비밀번호 변경
const updateUserpassword = async (req, res) => {
	const userId = Number(req.user_id)
	const password = req.body.password

	if (!userId) return res.status(400).json({ status: 400, message: 'invalid_user_id', data: null })
	if (!password) return res.status(400).json({ status: 400, message: 'invalid_password', data: null })

	const check = await checkUserIdModel(userId)
	if (check) return res.status(404).json({ status: 404, message: 'not_found_user', data: null })
	if (check === -1) return res.status(500).json({ status: 500, message: 'internal_server_error', data: null })

	const success = await updateUserPasswordModel({ userId, password })
	if (success === -1) return res.status(500).json({ status: 500, message: 'internal_server_error', data: null })
	req.session.destroy()
	return res.status(201).json({
		status: 201,
		message: 'change_user_password_success',
		data: null
	})
}

//회원탈퇴
const deleteUser = async (req, res) => {
	const userId = Number(req.session.user.user_id)

	if (!userId) return res.status(400).json({ status: 400, message: 'invalid_user_id', data: null })

	const isUser = await checkUserModel(userId)
	if (!isUser) {
		return res.status(404).json({ status: 404, message: 'not_found_user', data: null })
	}
	if (isUser === -1) return res.status(500).json({ status: 500, message: 'internal_server_error', data: null })

	const isSuccess = await deleteUserModel(userId)

	if (isSuccess) {
		req.session.destroy()
		return res.status(200).json({ status: 200, message: 'delete_user_data_success', data: null })
	}
	if (isSuccess === -1) return res.status(500).json({ status: 200, message: 'internal_server_error', data: null })
	if (!isSuccess) return res.status(404).json({ status: 404, message: 'not_found_user', data: null })
}

const duplicateEmail = async (req, res) => {
	const check = await checkUserEmailModel(req.params.email)
	if (check) return res.status(400).json({ status: 400, message: 'already_exist_email', data: null })
	if (check === -1) return res.status(500).json({ status: 200, message: 'internal_server_error', data: null })
	return res.status(200).json({ status: 200, message: 'available_email', data: null })
}

const duplicateNickname = async (req, res) => {
	const nickname = req.params.nickname
	const userId = Number(req.session.user.user_id) ?? null
	if (!userId) return res.status(404).json({ status: 404, message: 'invalid_user', data: null })

	const user = await checkUserModel(userId)
	if (user === -1) return res.status(500).json({ status: 200, message: 'internal_server_error', data: null })
	if (user.nickname === nickname) return res.status(200).json({ status: 200, message: 'same_nickname', data: null })

	const check = await checkUserNicknameModel(nickname)
	if (check === -1) return res.status(500).json({ status: 200, message: 'internal_server_error', data: null })
	if (check) return res.status(400).json({ status: 400, message: 'already_exist_nickname', data: null })
	return res.status(200).json({ status: 200, message: 'available_nickname', data: null })
}

const duplicateSignUpNickname = async (req, res) => {
	const check = await checkUserNicknameModel(nickname)
	if (check === -1) return res.status(500).json({ status: 200, message: 'internal_server_error', data: null })
	if (check) return res.status(400).json({ status: 400, message: 'already_exist_nickname', data: null })
	return res.status(200).json({ status: 200, message: 'available_nickname', data: null })
}

const logOut = (req, res) => {
	req.session.destroy()
	res.status(200).json({ status: 200, message: 'log_out_success', data: null })
}

const checkLogIn = (req, res) => {
	if (req.session.user) return res.status(200).json({ status: 200, message: 'authenticated', data: '' })

	return res.status(401).json({ status: 401, message: 'unauthenticated', data: '' })
}

const getMyCount = async (req, res) => {
	const data = await getUserWriteCount(req.session.user.user_id)
	if (getUserWriteCount === -1)
		return res.status(500).json({ status: 200, message: 'internal_server_error', data: null })
	return res.status(200).json({ status: 200, message: '', data })
}

const userController = {
	// getUsers,
	getUser,
	addUser,
	logInUser,
	updateUserProfile,
	duplicateSignUpNickname,
	updateUserpassword,
	deleteUser,
	duplicateEmail,
	duplicateNickname,
	logOut,
	checkLogIn,
	getMyCount
}

module.exports = userController
