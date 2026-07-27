package kr.adapterz.springboot.global.seed;

import kr.adapterz.springboot.like.Like;
import kr.adapterz.springboot.like.LikeRepository;
import kr.adapterz.springboot.post.Post;
import kr.adapterz.springboot.post.PostRepository;
import kr.adapterz.springboot.post.PostView;
import kr.adapterz.springboot.post.PostViewRepository;
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
import java.util.Random;

@Component
@Profile("seed-1000")
@RequiredArgsConstructor
public class SeedData1000Runner implements CommandLineRunner {

    private static final int USER_COUNT = 300;
    private static final int POST_COUNT = 1000;

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final PostViewRepository postViewRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        List<User> users = createUsers();
        createRankingCheckPosts(users);
        createRandomPosts(users);
    }

    private List<User> createUsers() {
        List<User> users = new ArrayList<>();

        users.add(new User(
                "jinxxang123@gmail.com",
                passwordEncoder.encode("xx28072807!"),
                "jay",
                null
        ));
        users.add(new User(
                "mini@test.com",
                passwordEncoder.encode("password123!"),
                "mini",
                null
        ));

        for (int i = 3; i <= USER_COUNT; i++) {
            users.add(new User(
                    "seed" + i + "@test.com",
                    passwordEncoder.encode("password123!"),
                    "seed" + i,
                    null
            ));
        }

        return userRepository.saveAll(users);
    }

    private void createRankingCheckPosts(List<User> users) {
        User jay = users.get(0);
        User mini = users.get(1);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime today = now.minusHours(1);
        LocalDateTime yesterday = now.minusDays(1);
        LocalDateTime lastWeek = now.minusWeeks(1);

        Post postA = postRepository.save(new Post(
                jay,
                "Post A - 오늘 Hot 1위 예상",
                "오늘 좋아요 5개, 오늘 조회 20개입니다.",
                today
        ));
        addLikes(postA, users, 0, 5, today);
        addViews(postA, users, 0, 20, today);

        Post postB = postRepository.save(new Post(
                mini,
                "Post B - 오늘 Hot 2위 예상",
                "오늘 좋아요 3개, 오늘 조회 100개입니다.",
                today
        ));
        addLikes(postB, users, 20, 3, today);
        addViews(postB, users, 20, 100, today);

        Post postC = postRepository.save(new Post(
                jay,
                "Post C - 주간 Hot 1위 예상",
                "어제 좋아요 10개, 어제 조회 50개입니다.",
                yesterday
        ));
        addLikes(postC, users, 120, 10, yesterday);
        addViews(postC, users, 120, 50, yesterday);

        Post postD = postRepository.save(new Post(
                mini,
                "Post D - 지난주 인기글",
                "지난주 좋아요 100개, 지난주 조회 1000개입니다.",
                lastWeek
        ));
        addLikes(postD, users, 200, 100, lastWeek);
        addViews(postD, users, 0, 300, lastWeek);
    }

    private void createRandomPosts(List<User> users) {
        Random random = new Random(42);
        LocalDateTime now = LocalDateTime.now();

        for (int i = 5; i <= POST_COUNT; i++) {
            User author = users.get(random.nextInt(users.size()));
            LocalDateTime postCreatedAt = randomDate(now, random);

            Post post = postRepository.save(new Post(
                    author,
                    "Seed Post " + i,
                    "랭킹 성능 확인용 seed 게시글입니다. index=" + i,
                    postCreatedAt
            ));

            int likeCount = random.nextInt(21);
            int viewCount = random.nextInt(51);

            addLikes(post, users, random.nextInt(users.size()), likeCount, randomDate(now, random));
            addViews(post, users, random.nextInt(users.size()), viewCount, randomDate(now, random));
        }
    }

    private LocalDateTime randomDate(LocalDateTime now, Random random) {
        int bucket = random.nextInt(10);

        if (bucket < 3) {
            return now.minusHours(random.nextInt(23) + 1L);
        }

        if (bucket < 7) {
            return now.minusDays(random.nextInt(6) + 1L);
        }

        if (bucket < 9) {
            return now.minusDays(random.nextInt(7) + 8L);
        }

        return now.minusDays(random.nextInt(20) + 15L);
    }

    private void addLikes(Post post, List<User> users, int startIndex, int count, LocalDateTime createdAt) {
        for (int i = 0; i < count; i++) {
            User user = users.get((startIndex + i) % users.size());
            likeRepository.save(new Like(post, user, createdAt));
            post.increaseLikeCount();
        }
    }

    private void addViews(Post post, List<User> users, int startIndex, int count, LocalDateTime viewedAt) {
        for (int i = 0; i < count; i++) {
            User user = users.get((startIndex + i) % users.size());
            postViewRepository.save(new PostView(post, user, viewedAt));
            post.increaseViewCount();
        }
    }
}
