const express = require('express')
const path = require('path')
const userRouter = require('./routes/user.cjs')
const postRouter = require('./routes/post.cjs')
const commentRouter = require('./routes/comment.cjs')
const Sentry = require('@sentry/node')
const { nodeProfilingIntegration } = require('@sentry/profiling-node')
const cors = require('cors')
const cookieParser = require('cookie-parser')
const session = require('express-session')
const app = express()
const port = 8000
const timeout = require('connect-timeout')
const rateLimit = require('express-rate-limit')
const helmet = require('helmet')

require('dotenv').config()
const { db_info } = require('./config/config.cjs')

const limiter = rateLimit({
	windowMs: 15 * 60 * 1000,
	max: 100,
	message: 'too many request'
})

const MySQLStore = require('express-mysql-session')(session)
const sessionStore = new MySQLStore(db_info)

// CORS options
const corsOptions = {
	origin: 'http://localhost:3000',
	credentials: true,
	methods: ['GET', 'PUT', 'POST', 'PATCH', 'DELETE']
}

// Use CORS middleware before any other middleware
app.use(cors(corsOptions))

app.use(express.json())
app.use(cookieParser())
app.use(
	session({
		secret: process.env.SESSION_SECRET,
		saveUninitialized: true,
		resave: false,
		cookie: {
			secure: false,
			maxAge: 1000 * 60 * 60 * 24
		},
		store: sessionStore
	})
)

Sentry.init({
	dsn: process.env.SENTRY_DSN,
	integrations: [
		// enable HTTP calls tracing
		new Sentry.Integrations.Http({ tracing: true }),
		// enable Express.js middleware tracing
		new Sentry.Integrations.Express({ app }),
		nodeProfilingIntegration()
	],
	// Performance Monitoring
	tracesSampleRate: 1.0, //  Capture 100% of the transactions
	// Set sampling rate for profiling - this is relative to tracesSampleRate
	profilesSampleRate: 1.0
})

// The request handler must be the first middleware on the app
app.use(Sentry.Handlers.requestHandler())

// TracingHandler creates a trace for every incoming request
app.use(Sentry.Handlers.tracingHandler())

app.use(express.urlencoded({ extended: true }))
app.use(express.json({ limit: '50mb' }))
app.use(express.text())

app.use(timeout('3s'))
app.use((req, res, next) => {
	if (!req.timedout) next()
})

app.use((err, req, res, next) => {
	if (req.timedout) {
		res.status(503).send({ status: 503, message: 'request_time_out', data: null })
	} else {
		next(err)
	}
})

app.use(limiter)
app.use(helmet())

// 정적 파일 제공 설정
app.use('/images', express.static(path.join(__dirname, 'images')))

// All your controllers should live here
app.get('/', function rootHandler(req, res) {
	res.json({ message: 'Hello world!' }) // Ensure a valid JSON response
})

// /api 경로용 라우터
const apiRouter = express.Router()
apiRouter.use('/users', userRouter)
apiRouter.use('/posts', postRouter)
apiRouter.use('/posts', commentRouter) // Fixed typo

// 공통 라우터
app.use('/api', apiRouter)

// Error handling middleware
app.use(function onError(err, req, res, next) {
	console.error(err.stack) // Log the error stack for debugging
	// The error id is attached to `res.sentry` to be returned
	// and optionally displayed to the user for support.
	res.statusCode = 500
	res.json({ error: res.sentry }) // Ensure a valid JSON response
})

// The error handler must be registered before any other error middleware and after all controllers
app.use(Sentry.Handlers.errorHandler())

app.listen(port, () => {
	console.log(`Server running on http://localhost:${port}`)
})
