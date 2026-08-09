const { getPostModel } = require('../model/posts.cjs')
const {
	getCommentsModel,
	checkCommentOwnerModel,
	addCommentModel,
	updateCommentModel,
	deleteCommentModel
} = require('../model/comments.cjs')

// 실제 controller 역할
const getComments = async (req, res) => {
	const postId = Number(req.params.postId)
	if (!postId) {
		return res.status(400).json({ status: 400, message: 'invalid_post_id', data: null })
	}

	const comments = await getCommentsModel(postId)
	if (comments === -1) return res.status(500).json({ status: 500, message: 'internal_sever_error', data: null })

	// TODO: 서버로 띄울 시 활성화 필요
	// comments.forEach((comment) => {
	//   comment.profile_image = comment.profile_image.replace(
	//     "http://localhost:8000",
	//     `https://${req.headers.host}`
	//   );
	// });
	return res.status(200).json({ status: 200, message: null, data: comments })
}

const addComment = async (req, res) => {
	const postId = Number(req.params.postId)
	const comment = req.body.comment
	const userId = Number(req.session.user.user_id)

	if (!userId) {
		return res.status(401).json({ status: 401, message: 'unauthenticated', data: null })
	}

	if (!postId) {
		return res.status(400).json({ status: 400, message: 'invalid_post_id', data: null })
	}

	const post = await getPostModel(postId)
	if (!post || post.length === 0) {
		return res.status(404).json({ status: 404, message: 'not_a_single_post', data: null })
	}

	if (!comment) {
		return res.status(400).json({ status: 400, message: 'invalid_comment', data: null })
	}

	const isSuccess = await addCommentModel({ postId, userId, comment })
	if (isSuccess === -1) return res.status(500).json({ status: 500, message: 'internal_server_error', data: null })
	if (isSuccess) return res.status(201).json({ status: 201, message: 'write_comment_success', data: null })
}

const updateComment = async (req, res) => {
	const commentId = Number(req.params.commentId)
	const commentContent = req.body.comment

	const isSuccess = await updateCommentModel({ commentId, commentContent })

	if (!isSuccess || isSuccess === -1)
		return res.status(500).json({ status: 500, message: 'internal_server_error', data: null })

	return res.status(200).json({ status: 200, message: 'update_comment_success', data: null })
}

const deleteComment = async (req, res) => {
	const commentId = Number(req.params.commentId)
	const postId = Number(req.params.postId)
	const isSuccess = await deleteCommentModel(postId, commentId)
	if (!isSuccess) return res.status(500).json({ status: 500, message: 'internal_server_error', data: null })

	return res.status(200).json({ status: 200, message: 'delete_comment_success', data: null })
}

const checkCommentOwner = async (req, res) => {
	const id = Number(req.body.commentId)
	const userId = Number(req.session.user.user_id)
	const check = await checkCommentOwnerModel({ userId, commentId: id })
	if (check === -1) return res.status(500).json({ status: 500, message: 'internal_sever_error', data: null })
	if (!check) return res.status(403).json({ status: 403, message: 'not_allowed', data: null })

	return res.status(200).json({ status: 200, message: 'is_owner', data: null })
}

module.exports = {
	getComments,
	addComment,
	updateComment,
	deleteComment,
	checkCommentOwner
}
