# Bamboo

> 개인적인 고민과 개발 경험을 주제로 사용자가 글을 작성하고, 댓글/대댓글로 소통하며, 실시간 채팅과 인기글 랭킹을 통해 활발한 커뮤니티 경험을 제공하는 웹 커뮤니티 프로젝트입니다.

## 프로젝트 소개

`Bamboo`는 단순 게시판 CRUD에서 시작해 인증, 권한, 소프트 삭제, 블라인드, 임시저장, 좋아요, 조회수 중복 방지, 인기글 랭킹, 실시간 채팅, 배포 자동화까지 확장한 개인 프로젝트입니다.

초기에는 HTTP와 REST API 설계, ERD 작성, Postman 검증을 중심으로 기능 단위를 작게 구현했습니다. 이후 H2 DB와 JPA를 도입하면서 인메모리/수동 SQL 중심 구조를 Entity 기반 구조로 전환했고, 최종적으로 Spring Boot, MySQL, Redis, WebSocket, Docker, GitHub Actions, AWS EC2 배포까지 연결했습니다.

프로젝트를 진행하면서 가장 중점적으로 본 부분은 “기능이 만들어졌는가”보다 “정말 동작하고 있는가”였습니다. JPA 변경 감지, 트랜잭션 적용 여부, 캐시/성능 개선 여부, 배치 실행 여부처럼 화면에 바로 드러나지 않는 동작을 로그, 테스트, EXPLAIN, Postman, 화면 버튼 등으로 확인하며 개선했습니다.

## 개발 인원 및 기간

- 개발 기간: 2026년 3주차 ~ 12주차
- 개발 인원: 프론트엔드/백엔드 1명
- 담당 범위: API 설계, ERD 설계, 백엔드 구현, 프론트엔드 구현, 배포, 테스트, 성능 개선, 피드백 반영

## Repository

- Back-end: `제출용 BE GitHub 링크 입력`
- Front-end: `제출용 FE GitHub 링크 입력`
- 배포 주소: `서비스 배포 URL 입력`
- 시연 영상: `시연 영상 링크 입력`
- API/회고 문서: `Notion 또는 docs 링크 입력`

## 사용 기술

### Back-end

- Java 21
- Spring Boot 3.5.4
- Spring Web
- Spring Data JPA
- Spring Security
- Spring Batch
- WebSocket/STOMP
- Redis Pub/Sub
- MySQL
- H2
- JWT
- ShedLock
- p6spy
- Gradle
- JUnit 5
- Jacoco

### Front-end

- React 19
- Vite
- JavaScript
- CSS
- STOMP.js
- Fetch API 기반 API client

### Infra / DevOps

- AWS EC2
- Docker
- Docker Compose
- Nginx
- GitHub Actions
- Docker Hub

## 주요 기능

### 회원

- 회원가입
- 로그인
- JWT Access Token / Refresh Token 발급
- Access Token 만료 시 Refresh Token으로 재발급
- 내 정보 조회
- 프로필 수정
- 비밀번호 변경
- 회원 탈퇴
- 탈퇴 회원 작성글의 작성자명 `알 수 없음` 처리

### 게시글

- 게시글 작성
- 게시글 목록 조회
- 게시글 상세 조회
- 게시글 수정
- 게시글 삭제
- 삭제된 게시글은 목록/상세 조회에서 제외
- 블라인드 게시글은 대체 문구 또는 조회 제외 정책 적용
- 게시글 작성자 권한 검증
- 게시글 본문 `TEXT` 매핑
- 페이지네이션 적용

### 댓글 / 대댓글

- 댓글 작성
- 댓글 수정
- 댓글 삭제
- 대댓글 작성
- 대댓글 수정
- 대댓글이 존재하는 댓글 삭제 시 댓글 자체를 물리 삭제하지 않고 삭제 상태로 보존
- 게시글 댓글 수 증가/감소 반영

### 좋아요

- 게시글 좋아요
- 좋아요 취소
- 사용자별 게시글 좋아요 중복 방지
- `likes(user_id, post_id)` 유니크 제약 기반 중복 방지
- `posts.like_count` 누적 카운트 관리
- 동시성 문제를 줄이기 위해 `update posts set like_count = like_count + 1` 형태의 증가 쿼리 사용

### 조회수

- 게시글 상세 조회 시 조회수 증가
- 같은 사용자가 24시간 안에 다시 조회하면 조회수 증가 방지
- `post_views`는 사용자별 마지막 조회 시각 관리
- `post_view_events`는 조회 이벤트를 누적 저장하여 기간별 조회수 집계에 사용

### 신고 / 블라인드

- 게시글 신고
- 같은 사용자의 중복 신고 방지
- 신고 누적 기준 도달 시 게시글 블라인드 처리
- 블라인드 게시글은 제목/내용/작성자 정보 노출을 제한
- 인기글 조회 시 삭제/블라인드 상태를 다시 확인하여 오래된 랭킹 데이터가 사용자에게 노출되지 않도록 처리

### 임시저장

- 게시글 임시저장
- 임시저장 조회
- 임시저장 삭제
- 임시저장 발행
- 제목만 있어도 임시저장 가능

### 인기글 랭킹

- DAILY / WEEKLY 인기글 제공
- 좋아요 수와 조회수를 점수로 계산
- 기간별 좋아요 수와 기간별 조회 이벤트 수를 기반으로 랭킹 계산
- Spring Batch 기반 랭킹 갱신
- DAILY Step과 WEEKLY Step을 분리하여 기간별 트랜잭션 경계 관리
- ShedLock으로 중복 스케줄 실행 방지
- 배치 주기를 설정값으로 분리
- 배치 1회 실행 시간 로그 기록
- MySQL EXPLAIN ANALYZE로 집계 쿼리 성능 확인

### 실시간 채팅

- 게시글별 채팅방
- WebSocket/STOMP 연결
- STOMP inbound channel에서 JWT 인증
- Redis Pub/Sub을 사용해 메시지 발행/구독 구조 구성
- 채팅 메시지 저장
- 과거 채팅 메시지 페이지 조회

## Back-end 폴더 구조

<details>
  <summary>폴더 구조 보기/숨기기</summary>
  <div markdown="1">

```text
src/main/java/kr/adapterz/springboot
├── auth
│   ├── TokenController.java
│   ├── TokenService.java
│   ├── RefreshToken.java
│   └── RefreshTokenRepository.java
├── chat
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── pubsub
│   ├── repository
│   └── service
├── comment
│   ├── Comment.java
│   ├── CommentController.java
│   ├── CommentRepository.java
│   ├── CommentService.java
│   └── dto
├── global
│   ├── ApiResponse.java
│   ├── GlobalExceptionHandler.java
│   ├── config
│   ├── exception
│   ├── redis
│   ├── security
│   ├── seed
│   └── websocket
├── like
│   ├── Like.java
│   ├── LikeController.java
│   ├── LikeRepository.java
│   └── LikeService.java
├── post
│   ├── PostRankingBatchScheduler.java
│   ├── RankingPeriod.java
│   ├── config
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   └── service
├── postDraft
│   ├── PostDraft.java
│   ├── PostDraftController.java
│   ├── PostDraftRepository.java
│   ├── PostDraftService.java
│   └── dto
├── report
│   ├── Report.java
│   ├── ReportController.java
│   ├── ReportRepository.java
│   ├── ReportService.java
│   └── dto
└── user
    ├── User.java
    ├── UserController.java
    ├── UserReader.java
    ├── UserRepository.java
    ├── UserService.java
    └── dto
```

  </div>
</details>

## Front-end 폴더 구조

<details>
  <summary>폴더 구조 보기/숨기기</summary>
  <div markdown="1">

```text
src
├── api
│   ├── authApi.js
│   ├── chatApi.js
│   ├── client.js
│   ├── commentApi.js
│   └── postApi.js
├── assets
│   └── hero.png
├── components
│   ├── ChatBox.jsx
│   ├── ChatInput.jsx
│   ├── ChatMessageItem.jsx
│   ├── ChatMessageList.jsx
│   ├── CommentItem.jsx
│   ├── CommentList.jsx
│   ├── ConfirmModal.jsx
│   ├── GlobalMessage.jsx
│   ├── Header.jsx
│   ├── Pagination.jsx
│   ├── PostCard.jsx
│   └── ProfileMenu.jsx
├── constants
│   └── messageMap.js
├── context
│   └── AuthContext.jsx
├── pages
│   ├── LoginPage.jsx
│   ├── PasswordEditPage.jsx
│   ├── PostCreatePage.jsx
│   ├── PostDetailPage.jsx
│   ├── PostEditPage.jsx
│   ├── PostListPage.jsx
│   ├── ProfileEditPage.jsx
│   ├── ProfilePage.jsx
│   ├── SignupPage.jsx
│   └── WelcomePage.jsx
└── utils
    ├── format.js
    ├── profileImage.js
    └── validation.js
```

  </div>
</details>

## API 설계

### Users

| Method | URI | 설명 |
|---|---|---|
| GET | `/users` | 사용자 목록 조회 |
| GET | `/users/me` | 내 정보 조회 |
| POST | `/users/register` | 회원가입 |
| POST | `/users/login` | 로그인 |
| PATCH | `/users/me` | 내 프로필 수정 |
| DELETE | `/users/me` | 회원 탈퇴 |
| PATCH | `/users/me/password` | 비밀번호 변경 |
| POST | `/users/token/refresh` | Access Token 재발급 |

### Posts

| Method | URI | 설명 |
|---|---|---|
| POST | `/posts` | 게시글 작성 |
| GET | `/posts` | 게시글 목록 조회 |
| GET | `/posts/rank` | 인기글 목록 조회 |
| GET | `/posts/{postId}` | 게시글 상세 조회 |
| PATCH | `/posts/{postId}` | 게시글 수정 |
| DELETE | `/posts/{postId}` | 게시글 삭제 |

### Drafts

| Method | URI | 설명 |
|---|---|---|
| POST | `/posts/draft` | 임시저장 저장 |
| GET | `/posts/draft` | 내 임시저장 조회 |
| DELETE | `/posts/draft` | 임시저장 삭제 |
| POST | `/posts/draft/publish` | 임시저장 발행 |

### Comments

| Method | URI | 설명 |
|---|---|---|
| POST | `/posts/{postId}/comments` | 댓글 작성 |
| PATCH | `/comments/{commentId}` | 댓글 수정 |
| DELETE | `/comments/{commentId}` | 댓글 삭제 |
| POST | `/comments/{commentId}/replies` | 대댓글 작성 |
| PATCH | `/comments/{commentId}/replies/{replyId}` | 대댓글 수정 |

### Likes / Reports / Chat

| Method | URI | 설명 |
|---|---|---|
| POST | `/posts/{postId}/likes` | 좋아요 |
| DELETE | `/posts/{postId}/likes` | 좋아요 취소 |
| POST | `/posts/{postId}/reports` | 게시글 신고 |
| GET | `/posts/{postId}/chat/messages` | 채팅 메시지 조회 |
| WS | `/ws` | STOMP WebSocket 연결 |

## 데이터베이스 설계

### 주요 Entity

- `users`: 사용자 정보, 탈퇴 여부, 프로필 이미지
- `posts`: 게시글, 누적 좋아요 수, 누적 댓글 수, 누적 조회 수, 블라인드 여부, 삭제 시각
- `comments`: 댓글/대댓글, 부모 댓글 참조, 삭제 상태
- `likes`: 사용자별 게시글 좋아요
- `reports`: 사용자별 게시글 신고
- `post_views`: 사용자별 마지막 조회 시각
- `post_view_events`: 조회 이벤트 누적 기록
- `post_drafts`: 사용자별 임시저장 글
- `post_rankings`: 기간별 인기글 랭킹 결과
- `chat_messages`: 게시글별 채팅 메시지
- `refresh_tokens`: Refresh Token 저장

### 설계 과정에서 반영한 점

- 좋아요 중복 방지를 위해 `user_id + post_id` 복합 유니크 제약을 사용했습니다.
- 댓글과 대댓글은 하나의 댓글 테이블에서 `parent_comment_id`로 구분했습니다.
- 게시글/댓글 작성자 닉네임을 복제하지 않고 사용자 테이블을 참조하도록 정규화했습니다.
- 탈퇴 사용자는 작성한 컨텐츠를 바로 삭제하지 않고 작성자 표시만 `알 수 없음`으로 처리했습니다.
- 조회수 중복 방지와 기간별 조회수 집계를 분리하기 위해 `post_views`와 `post_view_events`를 나누었습니다.
- 인기글 조회 성능을 위해 실시간 집계 대신 `post_rankings` 파생 테이블에 배치 결과를 저장했습니다.

## 구현 과정

### 3주차: Web 정리 및 API 설계

HTTP, URI/URL, REST API, 상태 코드, 쿠키/세션, JWT, HTTPS, 캐시 등을 정리하고 커뮤니티 서비스의 API를 먼저 설계했습니다. 이때 생성은 `201`, 수정은 `200`, 인증이 필요한 API는 `Authorization` 헤더 명시가 필요하다는 피드백을 받으며 API 계약을 더 구체적으로 보는 계기가 되었습니다.

### 4주차: API 구현

3주차 API 설계를 기반으로 회원, 게시글, 댓글 기능을 구현하고 Postman으로 요청/응답을 검증했습니다. 초기에는 인증을 단순화하기 위해 요청 body의 userId를 사용했지만, 이후 JWT 인증으로 전환할 때 보안상 한계를 명확히 인지하고 구조를 바꾸었습니다. 게시글 수정/삭제 권한 검증, 신고 API 누락, 블라인드 코드가 실제 호출되지 않는 문제도 피드백을 통해 보완했습니다.

### 5주차: ERD 작성

사용자, 게시글, 댓글, 좋아요, 신고, 세션/토큰 흐름을 ERD로 정리했습니다. 이 과정에서 NOT NULL, UNIQUE를 단순 코멘트가 아니라 실제 DB 제약으로 걸어야 한다는 점을 배웠고, 좋아요 중복 방지와 신고 중복 방지를 DB 제약과 서비스 검증 양쪽에서 다루도록 개선했습니다.

### 6주차: Entity 전환과 JPA 적용

기존 인메모리/수동 처리 구조를 H2 DB와 JPA Entity 기반 구조로 전환했습니다. `persist`, `flush`, `clear`, 1차 캐시, 더티 체킹이 실제 SQL에 어떤 영향을 주는지 p6spy 로그로 확인했습니다. 이 과정에서 `@Transactional`이 없으면 soft delete와 update가 응답상 성공처럼 보여도 실제 DB에 반영되지 않을 수 있다는 문제를 확인하고 서비스 트랜잭션 경계를 보강했습니다.

### 7주차: 프론트엔드 구현 및 API 연동

Vanilla JS 기반으로 화면 요구사항을 구현하며 이벤트 처리와 Fetch API 연동을 진행했습니다. 이후 React 전환 과정에서는 화면별 요구사항과 API 계약을 더 명확히 나누어야 한다는 점을 배웠습니다. 현재 React 프론트엔드는 인증 상태를 `AuthContext`에서 관리하고, API 호출은 `api/client.js`를 중심으로 분리했습니다.

### 8~9주차: 인증, 권한, 사용자 경험 개선

JWT 기반 인증으로 전환하고 Access Token / Refresh Token 구조를 구현했습니다. 게시글 작성, 수정, 삭제, 좋아요, 신고, 임시저장 등 인증이 필요한 요청은 JWT 필터를 거치도록 구성했습니다. 프론트엔드에서는 로그인 복원, 내 정보 조회, 프로필 수정, 비밀번호 변경, 회원 탈퇴 흐름을 구현했습니다.

### 10주차: 배포 및 인기글 기능

React와 Spring Boot를 Dockerfile로 빌드하고 Docker Compose, Nginx, AWS EC2 배포 구조를 구성했습니다. 인기글 기능은 좋아요 수와 조회 수를 가중합으로 계산하는 방식으로 시작했고, DAILY/WEEKLY 기간을 나누어 조회할 수 있게 했습니다. 단순히 화면이 빨라진 것처럼 보이는 데서 멈추지 않고, 실제 캐시/쿼리/배치가 영향을 준 것인지 버튼과 로그로 확인했습니다.

### 11주차: 성능 개선과 피드백 반영

좋아요/조회수 집계 성능을 개선하기 위해 인덱스와 EXPLAIN ANALYZE를 확인했습니다. `post_views`는 사용자별 마지막 조회 시각만 남기기 때문에 기간별 조회 이벤트 수를 정확히 세기 어렵다는 문제를 발견했고, 조회 이벤트를 누적하는 `post_view_events`를 도입했습니다. 또한 누적 카운트와 기간 카운트가 화면에서 섞이지 않도록 응답 기준을 정리했습니다.

### 12주차: 배치, 실시간 채팅, 최종 안정화

인기글 랭킹을 매번 API 요청 때 계산하지 않고 Spring Batch로 `post_rankings`에 저장하도록 구성했습니다. DAILY와 WEEKLY Step을 분리해 한 기간 실패가 다른 기간까지 롤백되지 않게 했고, 스케줄 주기와 ShedLock 값을 설정으로 분리했습니다. 배치 실행 시간 로그를 남겨 배치 주기와 실제 수행 시간을 비교할 수 있게 했습니다. 또한 WebSocket/STOMP와 Redis Pub/Sub 기반으로 게시글별 실시간 채팅 기능을 구현했습니다.

## 트러블 슈팅

### 1. JPA 변경 감지가 DB에 반영되지 않던 문제

게시글 수정과 삭제는 Entity의 필드를 변경하는 방식으로 구현했지만, Service에 트랜잭션이 없으면 더티 체킹이 동작하지 않아 DB에 반영되지 않았습니다. 응답은 메모리 객체 기준으로 생성되기 때문에 겉으로는 성공처럼 보였지만, 다시 조회하면 수정 전 데이터가 남아 있는 문제가 생겼습니다.

해결 방법:

- Service 계층에 `@Transactional` 적용
- 조회 전용 메서드는 `@Transactional(readOnly = true)`로 분리
- 수정/삭제 테스트가 실제 운영 흐름을 잡을 수 있도록 통합 테스트 확인

### 2. 테스트에서는 통과하지만 운영에서 실패할 수 있는 구조

테스트 클래스에 `@Transactional`이 붙어 있으면 테스트 안에서는 트랜잭션이 열려 있어 서비스 코드의 트랜잭션 누락이 가려질 수 있었습니다. 이 문제를 통해 테스트 환경이 실제 운영 흐름을 과도하게 대신해버릴 수 있다는 점을 알게 되었습니다.

해결 방법:

- 서비스 메서드의 트랜잭션 책임을 명확히 함
- 테스트 통과만으로 끝내지 않고 실제 요청/응답과 DB 상태 재조회까지 확인

### 3. 기간별 조회수 집계가 정확하지 않던 문제

초기에는 `post_views`의 `viewed_at`을 기준으로 기간별 조회수를 세려고 했습니다. 하지만 `post_views`는 같은 사용자가 같은 글을 다시 보면 기존 row의 `viewed_at`이 갱신되는 구조라, 특정 기간의 총 조회 이벤트 수가 아니라 “그 기간에 마지막으로 본 사용자 수”에 가까운 값이 나왔습니다.

해결 방법:

- 24시간 중복 조회 방지용 `post_views` 유지
- 기간별 조회 이벤트 집계용 `post_view_events` 추가
- 랭킹 배치 집계 쿼리를 `post_view_events.viewed_at` 기준으로 변경
- 문서와 EXPLAIN SQL도 실제 집계 테이블 기준으로 수정

### 4. 인기글 랭킹 조회 시 삭제/블라인드 글이 남는 문제

배치가 실행될 때는 `deleted_at is null`, `blinded = false` 조건으로 필터링했지만, 다음 배치 전까지 게시글 상태가 바뀌면 랭킹 테이블에 남은 오래된 데이터가 조회될 수 있었습니다.

해결 방법:

- 랭킹 조회 쿼리에서도 `p.deletedAt is null`, `p.blinded = false` 조건 추가
- `PostListResponse.blinded`를 하드코딩하지 않고 실제 게시글 필드 기준으로 응답

### 5. 인기글 응답에서 기간 카운트와 누적 카운트가 섞이던 문제

인기글 정렬은 기간별 좋아요/조회수로 계산하지만, 사용자가 화면에서 보는 숫자는 최신 탭과 인기 탭에서 같은 기준이어야 했습니다. 기존에는 인기글 응답에서 좋아요/조회수는 기간값, 댓글 수는 누적값이라 한 카드 안에 기준이 섞였습니다.

해결 방법:

- 랭킹 테이블은 정렬 기준으로 사용
- 화면 표시용 좋아요/조회/댓글 수는 `Post`의 누적값으로 통일
- 랭킹 계산 기준과 화면 표시 기준을 분리

### 6. 배치 트랜잭션 경계 문제

DAILY와 WEEKLY 랭킹을 하나의 Step 안에서 처리하면 한쪽 기간에서 실패했을 때 다른 기간까지 같이 롤백될 수 있고, 데이터가 커질수록 하나의 큰 트랜잭션이 오래 유지될 수 있습니다.

해결 방법:

- `dailyRankingRefreshStep`
- `weeklyRankingRefreshStep`
- Job에서 Daily Step 이후 Weekly Step 실행
- 기간별 삭제/삽입은 하나의 트랜잭션으로 묶되, DAILY와 WEEKLY는 Step 단위로 분리

### 7. 테스트 환경에서 Redis 연결 실패

CI에서 `./gradlew clean test` 실행 시 Redis 서버가 없는데 `RedisMessageListenerContainer`가 시작되며 Redis 연결을 시도해 ApplicationContext 로딩이 실패했습니다.

해결 방법:

- 통합 테스트에 `@ActiveProfiles("h2")` 적용
- Redis listener 설정은 `@Profile("!h2")`로 테스트 프로파일에서 제외
- CI와 동일한 `./gradlew clean test --stacktrace`로 검증

### 8. 배치 주기 하드코딩 문제

스케줄 주기가 코드에 직접 들어가 있으면 로컬 확인용 10초와 운영용 5분을 환경별로 나누기 어렵습니다. 배치 실행 시간이 주기를 넘는지도 확인할 수 없었습니다.

해결 방법:

- `ranking.batch.fixed-rate-ms` 설정값으로 분리
- H2/local과 MySQL/운영 설정 분리
- 배치 실행 전후 `System.nanoTime()`으로 `elapsedMs` 로그 기록

## 성능 개선

### 인기글 집계 개선 전

인기글 API 요청 시 게시글, 좋아요, 조회수 테이블을 직접 조인하고 기간 조건으로 집계하면 게시글 수가 늘어날수록 매 요청마다 비용이 커졌습니다. 특히 살아 있는 게시글 전체를 대상으로 조인, 그룹화, 정렬 후 상위 몇 건만 자르는 구조라 단순 인덱스만으로 한계가 있었습니다.

### 적용한 개선

- `likes(created_at, post_id)` 계열 인덱스 검토
- `post_view_events(viewed_at, post_id)` 인덱스 적용
- EXPLAIN ANALYZE로 쿼리 실행 계획 확인
- `post_rankings` 파생 테이블 도입
- Spring Batch로 DAILY/WEEKLY 랭킹 주기적 갱신
- API는 `post_rankings`를 조회하고 원본 `posts` 상태를 재검증

### 확인 방법

```bash
mysql -uroot --vertical < explain-popular.sql
mysql -uroot --vertical < explain-popular-optimized.sql
mysql -uroot --vertical < explain-popular-batch.sql
```

```bash
curl -w "\nTotal: %{time_total}s\n" -o /dev/null -s "http://localhost:8080/posts/rank?period=WEEKLY&size=5"
```

## 테스트

### 테스트 범위

- 회원가입/로그인 통합 테스트
- Refresh Token 재발급 통합 테스트
- 게시글 작성/조회/상세 조회 테스트
- 조회수 24시간 중복 방지 테스트
- 좋아요/좋아요 취소 테스트
- 댓글 작성/삭제 시 댓글 수 증감 테스트
- 임시저장 저장/발행 테스트
- 신고 누적 블라인드 테스트
- 인기글 정렬/기간 필터/중복 저장 방지 테스트
- 단위 테스트와 통합 테스트

### 실행 명령어

```bash
./gradlew clean test
```

### 테스트 결과

```text
BUILD SUCCESSFUL
45 tests completed
```

## 실행 방법

### Back-end

```bash
./gradlew bootRun --args='--spring.profiles.active=h2'
```

MySQL 환경:

```bash
export DB_URL='jdbc:mysql://localhost:3306/bamboo'
export DB_USERNAME='root'
export DB_PASSWORD='password'

./gradlew bootRun --args='--spring.profiles.active=mysql'
```

### Front-end

```bash
npm install
npm run dev
```

### Docker

```bash
docker build -t community-backend .
docker run -p 8080:8080 community-backend
```

## 배포

GitHub Actions는 다음 순서로 구성했습니다.

1. `main` 브랜치 push 또는 PR 발생
2. Java 21 환경에서 `./gradlew clean test` 실행
3. 테스트 성공 시 Docker image build
4. Docker Hub push
5. EC2 서버 SSH 접속
6. `docker compose pull backend`
7. `docker compose up -d --force-recreate --no-deps backend`
8. 프론트엔드 재시작

운영 환경은 `SPRING_PROFILES_ACTIVE=mysql`을 사용하고, DB 정보와 JWT secret은 환경변수로 주입합니다.

## 서비스 화면

> 아래 영역은 최종 제출 전에 실제 스크린샷 링크로 교체하면 됩니다.

### 로그인 / 회원가입

| 로그인 | 회원가입 |
|---|---|
| `이미지 링크 입력` | `이미지 링크 입력` |

### 게시글 목록 / 인기글

| 최신글 목록 | DAILY 인기글 | WEEKLY 인기글 |
|---|---|---|
| `이미지 링크 입력` | `이미지 링크 입력` | `이미지 링크 입력` |

### 게시글 작성 / 상세 / 수정 / 삭제

| 작성 | 상세 | 수정 | 삭제 |
|---|---|---|---|
| `이미지 링크 입력` | `이미지 링크 입력` | `이미지 링크 입력` | `이미지 링크 입력` |

### 댓글 / 대댓글 / 좋아요 / 신고

| 댓글 | 대댓글 | 좋아요 | 신고 |
|---|---|---|---|
| `이미지 링크 입력` | `이미지 링크 입력` | `이미지 링크 입력` | `이미지 링크 입력` |

### 임시저장 / 프로필 / 채팅

| 임시저장 | 프로필 수정 | 실시간 채팅 |
|---|---|---|
| `이미지 링크 입력` | `이미지 링크 입력` | `이미지 링크 입력` |

## 회고

처음에는 API 설계나 ERD 작성도 “정답이 있는 양식”처럼 느껴졌습니다. 하지만 주차가 지나면서 설계는 단순히 표를 채우는 일이 아니라, 실제 사용자가 어떤 요청을 보내고 서버가 어떤 책임을 져야 하는지 결정하는 과정이라는 점을 알게 되었습니다. 회원가입 화면으로 이동하는 것은 클라이언트 라우팅의 책임이고, 인증이 필요한 요청은 헤더와 토큰 계약을 명확히 적어야 하며, 생성/수정/삭제는 상태 코드부터 다르게 표현해야 한다는 피드백이 이후 구현의 기준이 되었습니다.

JPA를 도입하면서 가장 크게 배운 것은 “코드가 실행됐다”와 “DB에 반영됐다”는 서로 다르다는 점입니다. Entity 필드를 바꾸고 응답을 만들면 성공처럼 보이지만 트랜잭션이 없으면 실제 DB에는 반영되지 않을 수 있었습니다. 이 경험 이후로 p6spy 로그, flush/clear, 테스트, 재조회로 실제 동작을 확인하는 습관이 생겼습니다.

인기글 기능은 이번 프로젝트에서 가장 많이 고민한 기능입니다. 처음에는 API 요청 시점마다 좋아요와 조회수를 조인해 계산했지만, 데이터가 늘어날수록 쿼리 비용이 커지는 구조였습니다. 인덱스를 추가하고 EXPLAIN을 확인하면서도 “인덱스만으로 해결되지 않는 성질”이 있다는 것을 알게 되었고, 결국 기간별 랭킹 결과를 배치로 미리 계산해 `post_rankings`에 저장하는 방식으로 개선했습니다.

또한 조회수 집계에서도 단순히 `viewed_at`이 있으니 기간별 조회수를 셀 수 있다고 생각했지만, 사용자별 마지막 조회만 남기는 `post_views`로는 과거 이벤트가 덮어써진다는 문제를 확인했습니다. 이를 해결하기 위해 `post_view_events`를 분리했고, 중복 조회 방지와 기간별 이벤트 집계를 서로 다른 테이블의 책임으로 나누었습니다.

배포 과정에서는 Docker, Nginx, EC2, GitHub Actions를 연결하며 로컬에서 돌아가는 서비스와 운영에서 돌아가는 서비스의 차이를 경험했습니다. 특히 테스트가 Redis 연결 때문에 CI에서 실패하면서, 테스트 프로파일에서는 외부 인프라 의존성을 끊어야 한다는 점을 배웠습니다. 이후 `h2` 테스트 프로파일과 Redis listener 비활성화를 적용해 CI에서도 동일하게 테스트가 통과하도록 만들었습니다.

이번 프로젝트를 통해 가장 오래 가져갈 습관은 구현 후 검증입니다. 캐시가 정말 적용됐는지, 배치가 실제로 돌고 있는지, 트랜잭션이 걸렸는지, 인덱스를 타는지, 테스트가 운영 환경과 분리되는지 직접 확인했습니다. 화면에 에러가 보이지 않는 문제일수록 로그와 테스트, 쿼리 실행 계획을 통해 확인해야 한다는 점을 체감했습니다.

## 향후 개선 사항

- 인기글 저장 범위를 상위 N개로 명확히 정하고 배치 insert와 API 페이지 정책을 일치시키기
- 실시간 채팅 메시지 읽음 처리와 입장/퇴장 이벤트 추가
- 이미지 업로드를 로컬 저장에서 S3 기반으로 전환
- API 문서 자동화 또는 Swagger 도입
- 운영 환경에서 Redis, MySQL 장애 상황에 대한 fallback/모니터링 강화
- 배치 실행 시간과 실패율을 별도 지표로 수집
- 프론트엔드 라우팅을 상태 기반 분기에서 React Router 기반으로 전환
