package kr.adapterz.springboot.global.seed;

import kr.adapterz.springboot.like.Like;
import kr.adapterz.springboot.like.LikeRepository;
import kr.adapterz.springboot.post.RankingPeriod;
import kr.adapterz.springboot.post.entity.Post;
import kr.adapterz.springboot.post.entity.PostView;
import kr.adapterz.springboot.post.entity.PostViewEvents;
import kr.adapterz.springboot.post.repository.PostViewEventsRepository;
import kr.adapterz.springboot.post.repository.PostViewRepository;
import kr.adapterz.springboot.post.repository.PostRepository;
import kr.adapterz.springboot.user.User;
import kr.adapterz.springboot.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("seed-ranking-demo")
@RequiredArgsConstructor
public class SeedRankingDemoRunner implements CommandLineRunner {

    private static final String DEMO_OWNER_EMAIL = "ranking-demo-owner@test.com";
    private static final int USER_COUNT = 80;

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final PostViewRepository postViewRepository;
    private final PostViewEventsRepository postViewEventsRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.findByEmail(DEMO_OWNER_EMAIL).isPresent()) {
            return;
        }

        List<User> users = createUsers();
        createRankingDemoPosts(users);
    }

    private List<User> createUsers() {
        List<User> users = new ArrayList<>();

        users.add(new User(
                DEMO_OWNER_EMAIL,
                passwordEncoder.encode("password123!"),
                "rankingOwner",
                null
        ));

        for (int i = 1; i < USER_COUNT; i++) {
            users.add(new User(
                    "ranking-demo-user" + i + "@test.com",
                    passwordEncoder.encode("password123!"),
                    "rankUser" + i,
                    null
            ));
        }

        return userRepository.saveAll(users);
    }

    private void createRankingDemoPosts(List<User> users) {
        User owner = users.get(0);
        LocalDateTime today = RankingPeriod.DAILY.getStartDateTime().plusHours(10);
        LocalDateTime weeklyOnly = getWeeklyOnlyDate();

        createPostWithStats(
                owner,
                users,
                "[DAILY 1위 예상] 오늘 리뷰 요청 - Redis 채팅 연결 문제",
                "오늘 좋아요 6개, 오늘 조회 45개입니다. DAILY 점수 105점입니다.",
                today,
                6,
                45
        );

        createPostWithStats(
                owner,
                users,
                "[DAILY 2위 예상] 오늘 리뷰 요청 - 이미지 업로드 API",
                "오늘 좋아요 5개, 오늘 조회 35개입니다. DAILY 점수 85점입니다.",
                today.plusMinutes(10),
                5,
                35
        );

        createPostWithStats(
                owner,
                users,
                "[DAILY 3위 예상] 오늘 리뷰 요청 - Markdown 에디터",
                "오늘 좋아요 3개, 오늘 조회 30개입니다. DAILY 점수 60점입니다.",
                today.plusMinutes(20),
                3,
                30
        );

        createPostWithStats(
                owner,
                users,
                "[WEEKLY 1위 예상] 이번 주 리뷰 요청 - 랭킹 배치 트랜잭션",
                "이번 주 이전 날짜 좋아요 9개, 조회 65개입니다. WEEKLY 점수 155점이고 DAILY에는 잡히지 않습니다.",
                weeklyOnly,
                9,
                65
        );

        createPostWithStats(
                owner,
                users,
                "[WEEKLY 2위 예상] 이번 주 리뷰 요청 - 조회 이벤트 집계",
                "이번 주 이전 날짜 좋아요 8개, 조회 55개입니다. WEEKLY 점수 135점이고 DAILY에는 잡히지 않습니다.",
                weeklyOnly.plusMinutes(10),
                8,
                55
        );

        createPostWithStats(
                owner,
                users,
                "[WEEKLY 3위 예상] 이번 주 리뷰 요청 - 인덱스 적용",
                "이번 주 이전 날짜 좋아요 7개, 조회 50개입니다. WEEKLY 점수 120점이고 DAILY에는 잡히지 않습니다.",
                weeklyOnly.plusMinutes(20),
                7,
                50
        );
    }

    private LocalDateTime getWeeklyOnlyDate() {
        LocalDateTime dailyStart = RankingPeriod.DAILY.getStartDateTime();
        LocalDateTime weeklyStart = RankingPeriod.WEEKLY.getStartDateTime();
        LocalDateTime weeklyOnly = dailyStart.minusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0);

        if (weeklyOnly.isBefore(weeklyStart)) {
            return dailyStart.plusHours(9);
        }

        return weeklyOnly;
    }

    private void createPostWithStats(
            User owner,
            List<User> users,
            String title,
            String content,
            LocalDateTime eventTime,
            int likeCount,
            int viewCount
    ) {
        Post post = postRepository.save(new Post(
                owner,
                title,
                content,
                eventTime
        ));

        addLikes(post, users, likeCount, eventTime);
        addViews(post, users, viewCount, eventTime);
    }

    private void addLikes(Post post, List<User> users, int count, LocalDateTime createdAt) {
        for (int i = 0; i < count; i++) {
            User user = users.get(i);
            likeRepository.save(new Like(post, user, createdAt));
            post.increaseLikeCount();
        }
    }

    private void addViews(Post post, List<User> users, int count, LocalDateTime viewedAt) {
        for (int i = 0; i < count; i++) {
            User user = users.get(i);
            postViewRepository.save(new PostView(post, user, viewedAt));
            postViewEventsRepository.save(new PostViewEvents(post, user, viewedAt));
            post.increaseViewCount();
        }
    }
}
