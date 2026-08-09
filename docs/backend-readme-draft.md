# Bamboo Back-end

## Back-end 소개

- 개인적인 고민과 개발 경험을 주제로 사용자가 글을 작성하고 댓글, 대댓글, 좋아요, 신고, 실시간 채팅으로 소통할 수 있는 커뮤니티 서비스의 백엔드입니다.
- 초기 REST API 설계와 Postman 검증에서 시작해 Spring Boot, JPA, MySQL, JWT, Redis, WebSocket, Spring Batch, Docker, GitHub Actions 배포까지 직접 구현했습니다.
- 단순 CRUD 구현에서 끝내지 않고, 트랜잭션 적용 여부, JPA 변경 감지, N+1 쿼리, 기간별 조회수 집계, 인기글 배치 실행 여부, CI 테스트 실패 원인처럼 화면에 바로 드러나지 않는 문제를 로그와 테스트로 확인하며 개선했습니다.
- Controller, Service, Repository, DTO, Entity 역할을 분리하고, Entity를 API 응답에 직접 노출하지 않도록 DTO 응답 구조를 사용했습니다.

## 개발 인원 및 기간

- 개발 기간: 2026년 3주차 ~ 12주차
- 개발 인원: 1명
- 담당 범위: API 설계, ERD 설계, 백엔드 구현, 테스트, 성능 개선, 배포 자동화, 피드백 반영

## Repository

- Back-end Github: `제출용 BE GitHub 링크 입력`
- Front-end Github: `제출용 FE GitHub 링크 입력`
- 배포 주소: `배포 URL 입력`
- 시연 영상: `시연 영상 링크 입력`
- 회고/문서: `Notion 또는 문서 링크 입력`

## 사용 기술 및 Tools

- Java 21
- Spring Boot 3.5.4
- Spring Web
- Spring Data JPA
- Spring Security
- Spring Batch
- WebSocket / STOMP
- Redis Pub/Sub
- MySQL
- H2
- JWT
- ShedLock
- p6spy
- Gradle
- JUnit 5
- Jacoco
- Docker
- Docker Compose
- GitHub Actions
- AWS EC2

## 주요 기능

### Users

```text
- 회원가입
- 로그인
- JWT Access Token / Refresh Token 발급
- Refresh Token 기반 Access Token 재발급
- 내 정보 조회
- 프로필 수정
- 비밀번호 변경
- 회원 탈퇴
- 탈퇴 회원의 작성자명 "알 수 없음" 처리
```

### Posts

```text
- 게시글 작성
- 게시글 목록 조회
- 게시글 상세 조회
- 게시글 수정
- 게시글 삭제
- Soft Delete 적용
- 블라인드 게시글 응답 분기
- 작성자 권한 검증
- 페이지네이션 적용
- 게시글 본문 TEXT 매핑
```

### Comments

```text
- 댓글 작성
- 댓글 수정
- 댓글 삭제
- 대댓글 작성
- 대댓글 수정
- 부모 댓글과 대댓글을 하나의 comments 테이블에서 관리
- 대댓글이 존재하는 댓글은 삭제 상태로 보존
- 댓글 수 증감 처리
```

### Likes

```text
- 게시글 좋아요
- 게시글 좋아요 취소
- 사용자별 좋아요 중복 방지
- likes(user_id, post_id) 유니크 제약
- posts.like_count 누적값 관리
- 좋아요 수 동시성 문제를 줄이기 위한 update 쿼리 사용
```

### Views

```text
- 게시글 상세 조회 시 조회수 증가
- 같은 사용자가 24시간 안에 다시 조회하면 조회수 증가 방지
- post_views: 사용자별 마지막 조회 시각 관리
- post_view_events: 기간별 조회 이벤트 집계용 로그 테이블
```

### Reports

```text
- 게시글 신고
- 동일 사용자의 동일 게시글 중복 신고 방지
- 신고 누적 기준 도달 시 게시글 블라인드 처리
- 블라인드 게시글 정보 노출 제한
```

### Drafts

```text
- 게시글 임시저장
- 임시저장 조회
- 임시저장 삭제
- 임시저장 발행
- 제목만 있어도 임시저장 가능
```

### Ranking

```text
- DAILY / WEEKLY 인기글 랭킹
- 기간별 좋아요 수 + 기간별 조회 이벤트 수 기반 점수 계산
- Spring Batch로 post_rankings 집계 테이블 갱신
- DAILY Step / WEEKLY Step 분리
- ShedLock으로 중복 스케줄 실행 방지
- 스케줄 주기 설정값 분리
- 배치 실행 시간 elapsedMs 로그 기록
```

### Chat

```text
- 게시글별 실시간 채팅
- WebSocket/STOMP 연결
- STOMP 연결 시 JWT 인증
- Redis Pub/Sub 기반 메시지 발행/구독
- 채팅 메시지 저장
- 과거 채팅 메시지 페이지 조회
```

## 폴더 구조

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

### 주요 테이블

| 테이블 | 역할 |
|---|---|
| `users` | 사용자 정보, 탈퇴 상태, 프로필 이미지 |
| `posts` | 게시글 본문, 누적 좋아요/댓글/조회 수, 삭제/블라인드 상태 |
| `comments` | 댓글과 대댓글, 부모 댓글 참조, 삭제 상태 |
| `likes` | 사용자별 게시글 좋아요 |
| `reports` | 사용자별 게시글 신고 |
| `post_views` | 사용자별 마지막 조회 시각 |
| `post_view_events` | 조회 이벤트 누적 기록 |
| `post_drafts` | 사용자별 임시저장 글 |
| `post_rankings` | 기간별 인기글 배치 결과 |
| `chat_messages` | 게시글별 채팅 메시지 |
| `refresh_tokens` | Refresh Token 저장 |

### 설계 기준

- 댓글과 대댓글은 별도 테이블로 나누지 않고 `parent_comment_id`로 계층을 표현했습니다.
- 좋아요와 신고는 서비스 검증뿐 아니라 DB 유니크 제약으로 중복을 방지했습니다.
- 작성자 닉네임은 게시글/댓글 테이블에 복제하지 않고 사용자 테이블을 참조했습니다.
- 탈퇴 사용자의 컨텐츠는 삭제하지 않고 작성자 표시 정책으로 처리했습니다.
- 조회수 중복 방지와 기간별 조회 이벤트 집계를 분리하기 위해 `post_views`, `post_view_events`를 나누었습니다.
- 인기글 조회 성능을 위해 실시간 집계 대신 `post_rankings` 집계 테이블을 사용했습니다.

## 트러블 슈팅

### 1. API 명세와 실제 구현이 어긋난 문제

초기 API 설계에서는 상태 코드, 요청 헤더, 응답 필드명을 충분히 엄격하게 관리하지 못했습니다. 로그인 응답을 201로 설계하거나, 수정 API도 201로 작성하거나, 댓글 삭제 Method 오타가 있는 등 프론트엔드가 그대로 참고하면 혼란이 생길 수 있는 부분이 있었습니다. 또한 `authorDeleted`, `authordeleted`처럼 응답 필드명이 섞이면 프론트에서는 같은 의미라도 다른 키로 인식합니다.

해결:

- 생성은 `201`, 조회/수정/로그인은 `200`으로 기준을 정리했습니다.
- 인증이 필요한 API는 `Authorization` 헤더를 기준으로 명확히 구분했습니다.
- 응답 DTO 필드명을 camelCase 기준으로 통일했습니다.
- Controller 응답은 `ApiResponse<T>`와 DTO를 통해 내려주고 Entity 직접 노출을 피했습니다.

배운 점:

- API 명세는 단순 문서가 아니라 프론트엔드와 백엔드 사이의 계약입니다.
- 작은 오타나 상태 코드 불일치도 프론트 구현 오류로 이어질 수 있습니다.

### 2. 게시글 수정/삭제가 응답상 성공하지만 DB에 반영되지 않던 문제

게시글 수정과 삭제는 Entity의 메서드로 값을 변경하는 방식으로 구현했습니다. 그런데 Service에 트랜잭션이 없으면 JPA 더티 체킹이 동작하지 않아 DB에 update가 나가지 않았습니다. 응답은 메모리 객체 기준으로 만들어지기 때문에 호출한 쪽에서는 성공처럼 보였지만, 다시 조회하면 데이터가 그대로 남아 있었습니다.

해결:

- Service 계층에 `@Transactional`을 적용했습니다.
- 조회 전용 메서드는 `@Transactional(readOnly = true)`로 분리했습니다.
- p6spy 로그로 실제 update SQL이 나가는지 확인했습니다.

배운 점:

- JPA에서는 Entity 값을 바꾸는 것만으로 충분하지 않고, 트랜잭션 경계 안에서 변경되어야 DB 반영까지 이어집니다.
- 테스트에 `@Transactional`이 걸려 있으면 서비스의 트랜잭션 누락을 가릴 수 있어 실제 요청 흐름도 확인해야 합니다.

### 3. 테스트에서는 통과하지만 실제 서비스 버그를 놓칠 수 있는 문제

초기 테스트는 통합 테스트 자체가 트랜잭션을 열고 있었기 때문에, 서비스 코드에 트랜잭션이 없어도 변경 감지가 되는 것처럼 보일 수 있었습니다. 이 때문에 운영 코드에서는 실패할 수 있는 문제를 테스트가 잡지 못하는 상황이 생겼습니다.

해결:

- 서비스 메서드가 직접 트랜잭션 책임을 갖도록 수정했습니다.
- 테스트 결과뿐 아니라 DB 재조회, SQL 로그, 실제 API 호출 결과를 함께 확인했습니다.
- `./gradlew clean test`를 기준으로 CI와 같은 환경에서 검증했습니다.

배운 점:

- 테스트는 버그를 찾는 도구이지만, 테스트 환경이 실제 실행 환경과 다르면 오히려 문제를 숨길 수 있습니다.
- 테스트가 무엇을 보장하고 무엇을 보장하지 않는지 같이 봐야 합니다.

### 4. 좋아요 수 누적값의 동시성 문제

좋아요를 누를 때 `post.getLikeCount()` 값을 읽고 Entity 필드를 1 증가시키는 방식은 동시에 여러 사용자가 좋아요를 누르면 lost update가 발생할 수 있습니다. 예를 들어 두 사용자가 동시에 좋아요를 누르고 둘 다 기존 값 5를 읽으면, 좋아요 row는 2개 생겼는데 게시글의 like_count는 6으로 한 번만 증가할 수 있습니다.

해결:

- 좋아요 row는 `likes` 테이블에 저장합니다.
- `posts.like_count`는 화면 표시용 누적값으로 유지합니다.
- 증가/감소는 `update Post p set p.likeCount = p.likeCount + 1` 형태의 update 쿼리를 사용했습니다.
- 좋아요 중복은 `likes(user_id, post_id)` 유니크 제약과 service 검증으로 방지했습니다.

배운 점:

- 누적 카운트 필드는 편하지만 동시성 문제가 생기기 쉽습니다.
- 읽고 수정해서 저장하는 방식보다 DB에서 원자적으로 증가시키는 쿼리가 더 안전합니다.

### 5. 기간별 조회수 집계가 정확하지 않던 문제

인기글 랭킹에서 기간별 조회수를 계산할 때 처음에는 `post_views.viewed_at`을 기준으로 세려고 했습니다. 하지만 `post_views`는 같은 사용자가 같은 게시글을 다시 조회하면 기존 row의 `viewed_at`만 갱신하는 구조입니다. 그래서 특정 기간의 실제 조회 이벤트 수가 아니라 “그 기간에 마지막으로 본 사용자 수”에 가까운 값이 나왔습니다.

해결:

- `post_views`: 24시간 중복 조회 방지용 테이블
- `post_view_events`: 조회 이벤트 누적 저장용 테이블
- 인기글 랭킹 집계는 `post_view_events.viewed_at` 기준으로 변경했습니다.
- EXPLAIN SQL과 문서도 `post_view_events` 기준으로 맞췄습니다.

배운 점:

- 같은 `viewed_at` 컬럼이 있어도 테이블의 책임이 다르면 집계 의미가 달라집니다.
- 중복 방지용 상태 테이블과 이벤트 로그 테이블은 분리하는 편이 더 명확합니다.

### 6. 인기글 집계 쿼리 성능 문제

인기글을 API 요청 시점마다 계산하면 게시글, 좋아요, 조회수 테이블을 조인하고 그룹화한 뒤 정렬해야 합니다. 게시글 수가 많아질수록 요청 시간이 늘어났고, 주간 인기글은 체감될 정도로 느려졌습니다. 단순히 인덱스를 추가해도 살아 있는 게시글 전체를 대상으로 집계하고 정렬하는 구조적 비용은 남았습니다.

해결:

- `likes(created_at, post_id)` 계열 인덱스를 검토했습니다.
- `post_view_events(viewed_at, post_id)` 인덱스를 추가했습니다.
- MySQL `EXPLAIN ANALYZE`로 실행 계획을 확인했습니다.
- 실시간 집계 대신 Spring Batch로 `post_rankings` 테이블을 주기적으로 갱신했습니다.
- API는 집계 테이블을 조회하고 원본 게시글 상태를 한 번 더 확인합니다.

배운 점:

- 인덱스는 필요하지만 모든 성능 문제를 해결하지는 않습니다.
- 매 요청마다 계산할 필요가 없는 데이터는 배치로 미리 계산하는 구조가 더 적합할 수 있습니다.

### 7. 인기글 조회 시 삭제/블라인드 글이 남는 문제

배치 실행 시점에는 삭제되지 않았고 블라인드도 아니었던 글이 랭킹 테이블에 들어갈 수 있습니다. 그 뒤 다음 배치가 돌기 전 게시글이 삭제되거나 블라인드 처리되면, 랭킹 테이블에는 오래된 데이터가 남아 인기글 목록에 정상 글처럼 보일 수 있었습니다.

해결:

- 랭킹 조회 쿼리에서도 `p.deletedAt is null`, `p.blinded = false` 조건을 추가했습니다.
- `countQuery`에도 같은 조건을 넣어 Page 응답의 total 값이 어긋나지 않게 했습니다.
- 응답의 `blinded` 값은 하드코딩하지 않고 실제 `post.isBlinded()` 값을 사용했습니다.

배운 점:

- 배치 결과는 캐시성 데이터라 시간이 지나면 원본 상태와 달라질 수 있습니다.
- 조회 시점에도 사용자에게 보여줘도 되는 상태인지 재검증해야 합니다.

### 8. 인기글 응답에서 기간값과 누적값이 섞인 문제

랭킹 테이블의 `likeCount`, `viewCount`는 DAILY/WEEKLY 기간 안에서 계산된 값입니다. 반면 `commentCount`는 `Post`의 누적값이었습니다. 이 상태로 응답을 만들면 한 카드 안에서 좋아요/조회수는 기간값, 댓글 수는 누적값이 되어 기준이 섞입니다.

해결:

- 랭킹 테이블은 정렬 기준으로 사용합니다.
- 화면 표시용 좋아요 수, 조회 수, 댓글 수는 `Post`의 누적값으로 통일했습니다.
- 최신 탭과 인기 탭에서 같은 게시글의 표시 숫자가 다르게 보이지 않도록 했습니다.

배운 점:

- 랭킹 계산 기준과 사용자에게 보여주는 표시 기준은 분리해서 생각해야 합니다.
- 응답 필드의 의미가 화면에서 어떻게 보이는지까지 고려해야 합니다.

### 9. 배치 트랜잭션 경계 문제

처음에는 DAILY와 WEEKLY 랭킹을 하나의 흐름에서 같이 갱신했습니다. 이렇게 하면 한쪽 기간에서 실패했을 때 두 기간이 함께 롤백될 수 있고, 데이터가 커질수록 하나의 큰 트랜잭션이 오래 유지됩니다.

해결:

- `dailyRankingRefreshStep`
- `weeklyRankingRefreshStep`
- Job에서 DAILY Step 이후 WEEKLY Step 실행
- 기간 하나의 삭제/삽입은 하나의 트랜잭션으로 묶고, DAILY와 WEEKLY는 Step 단위로 분리했습니다.

배운 점:

- 삭제와 삽입을 한 트랜잭션으로 묶는 것은 맞지만, 서로 독립적인 기간까지 하나로 묶을 필요는 없습니다.
- 배치에서는 실패 범위와 커밋 단위를 의도적으로 설계해야 합니다.

### 10. 배치 주기 하드코딩과 실행 시간 확인 문제

`@Scheduled(fixedRateString = "10000")`처럼 주기가 코드에 박혀 있으면 로컬 확인용 10초와 운영용 5분을 나누기 어렵습니다. 또한 배치가 한 번 도는 데 얼마나 걸리는지 로그가 없으면, 실행 시간이 주기를 넘는지 판단할 수 없습니다.

해결:

- `ranking.batch.fixed-rate-ms` 설정값으로 분리했습니다.
- H2/local과 MySQL/운영 설정에서 값을 다르게 둘 수 있게 했습니다.
- `System.nanoTime()`으로 시작/종료 시간을 계산해 `elapsedMs` 로그를 남겼습니다.
- ShedLock 시간도 설정값으로 분리했습니다.

배운 점:

- 운영과 로컬 확인 주기는 다를 수 있으므로 설정으로 분리해야 합니다.
- 배치는 실행 여부뿐 아니라 1회 소요 시간도 계속 확인해야 합니다.

### 11. CI 테스트에서 Redis 연결 실패

GitHub Actions에서 `./gradlew clean test`를 실행했을 때 Redis 서버가 없는데 `RedisMessageListenerContainer`가 시작되며 Redis 연결을 시도했습니다. 이 때문에 첫 번째 ApplicationContext 로딩이 실패했고, 이후 통합 테스트들이 연쇄적으로 실패했습니다.

해결:

- 통합 테스트에 `@ActiveProfiles("h2")`를 추가했습니다.
- Redis listener 설정은 `@Profile("!h2")`로 H2 테스트 프로파일에서는 뜨지 않게 했습니다.
- CI와 같은 `./gradlew clean test --stacktrace` 명령으로 재검증했습니다.

배운 점:

- 테스트 환경에서는 외부 인프라 의존성을 끊어야 합니다.
- 로컬에서 Redis가 떠 있어서 통과하는 테스트도 CI에서는 실패할 수 있습니다.

### 12. N+1 쿼리 문제

랭킹 배치 결과를 조회한 뒤 게시글과 작성자 정보를 접근할 때 fetch join이 없으면 각 row마다 게시글/작성자 조회가 추가로 발생할 수 있었습니다. API 응답은 정상이어도 데이터가 늘어나면 쿼리 수가 증가하는 문제가 생깁니다.

해결:

- 랭킹 조회 쿼리에 `join fetch r.post p`, `join fetch p.author`를 적용했습니다.
- 게시글 목록 조회에서도 작성자 fetch join과 Pageable을 함께 사용했습니다.
- 이미 조회한 `post.getAuthor()`를 사용하고 불필요한 user 재조회는 줄였습니다.

배운 점:

- 응답이 정상이라는 것과 쿼리가 효율적이라는 것은 다릅니다.
- fetch join, EntityGraph, DTO projection 중 현재 조회 목적에 맞는 방식을 선택해야 합니다.

## 성능 테스트

### 인기글 API 시간 측정

```bash
curl -w "\nTotal: %{time_total}s\n" -o /dev/null -s "http://localhost:8080/posts/rank?period=WEEKLY&size=5"
```

### EXPLAIN ANALYZE

```bash
mysql -uroot --vertical < explain-popular.sql
mysql -uroot --vertical < explain-popular-optimized.sql
mysql -uroot --vertical < explain-popular-batch.sql
```

### 테스트 실행

```bash
./gradlew clean test
```

현재 확인 결과:

```text
BUILD SUCCESSFUL
45 tests completed
```

## 실행 방법

### H2 로컬 실행

```bash
./gradlew bootRun --args='--spring.profiles.active=h2'
```

### MySQL 실행

```bash
export DB_URL='jdbc:mysql://localhost:3306/bamboo'
export DB_USERNAME='root'
export DB_PASSWORD='password'

./gradlew bootRun --args='--spring.profiles.active=mysql'
```

### Docker 실행

```bash
docker build -t community-backend .
docker run -p 8080:8080 community-backend
```

## 배포

GitHub Actions는 다음 흐름으로 구성했습니다.

```text
push / pull request
-> Java 21 설정
-> ./gradlew clean test
-> Docker image build
-> Docker Hub push
-> EC2 SSH 접속
-> docker compose pull backend
-> docker compose up -d --force-recreate --no-deps backend
```

운영 환경에서는 `SPRING_PROFILES_ACTIVE=mysql`을 사용하고, DB 접속 정보와 JWT secret은 환경변수로 관리합니다.

## 프로젝트 후기

백엔드에서 가장 크게 배운 점은 “작성한 코드가 정말 동작하는지 확인하는 과정”이었습니다. API 응답이 성공이어도 DB에 반영되지 않을 수 있고, 화면이 정상이어도 배치가 돌지 않을 수 있으며, 테스트가 통과해도 CI에서는 Redis 같은 외부 의존성 때문에 실패할 수 있었습니다.

JPA를 사용하면서 트랜잭션, 더티 체킹, 1차 캐시, flush/clear가 단순 이론이 아니라 실제 SQL과 직결된다는 것을 확인했습니다. p6spy 로그를 통해 INSERT, UPDATE, SELECT가 언제 나가는지 직접 보면서, 앞으로 성능 문제나 데이터 정합성 문제를 볼 때도 쿼리 로그를 먼저 확인해야 한다는 습관을 얻었습니다.

인기글 랭킹 기능은 단순 정렬 기능처럼 시작했지만, 기간별 집계, 조회 이벤트 저장, 인덱스, EXPLAIN, 배치, 트랜잭션 경계, 삭제/블라인드 상태 재검증까지 연결된 기능이었습니다. 이 기능을 구현하면서 기능 하나에도 데이터 모델, 쿼리 성능, 화면 표시 기준, 운영 주기까지 같이 설계해야 한다는 것을 배웠습니다.

배포 과정에서는 Docker와 GitHub Actions가 단순히 “자동으로 배포해주는 도구”가 아니라, 수동 배포에서 반복되던 실수와 환경 차이를 줄여주는 도구라는 점을 체감했습니다. 직접 EC2에 접속해 jar를 다시 빌드하고 실행하던 방식과 비교하면서, 이미지 빌드와 pull 기반 배포가 왜 필요한지 이해할 수 있었습니다.

## 향후 개선 사항

- 인기글을 상위 몇 위까지 저장/제공할지 정책값으로 명확히 분리
- Swagger 또는 REST Docs 기반 API 문서 자동화
- S3 기반 이미지 업로드 도입
- 배치 실패 알림과 실행 시간 모니터링 추가
- Redis/MySQL 장애 상황에 대한 운영 대응 전략 보강
- 랭킹 점수 공식에 댓글 수, 시간 감쇠 등 추가 기준 검토
