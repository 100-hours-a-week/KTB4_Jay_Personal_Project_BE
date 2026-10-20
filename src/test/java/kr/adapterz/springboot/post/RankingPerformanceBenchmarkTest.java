package kr.adapterz.springboot.post;

import kr.adapterz.springboot.post.service.PostService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringBootTest
@ActiveProfiles("h2")
@Testcontainers
class RankingPerformanceBenchmarkTest {

    @Container
    static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.4")
            .withDatabaseName("bamboo")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void mysqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> MYSQL.getJdbcUrl() + "?rewriteBatchedStatements=true");
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.batch.jdbc.initialize-schema", () -> "never");
        registry.add("spring.data.redis.repositories.enabled", () -> "false");
    }

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    PostService postService;

    @BeforeAll
    static void start() {
        MYSQL.start();
    }

    @Test
    void compareRequestTimeAggregationAndPrecomputedRankingLookup() {
        seed();
        buildRankings();

        // JIT / pool / MySQL buffer warm-up
        for (int i = 0; i < 10; i++) {
            currentLookup();
            oldStyleAggregation();
        }

        List<Double> currentMs = measure(50, this::currentLookup);
        List<Double> aggregateMs = measure(30, this::oldStyleAggregation);

        System.out.printf(
                "BAMBOO_BENCHMARK dataset(posts=10000, likes=50000, views=100000) " +
                "current_precomputed[p50=%.3fms,p95=%.3fms,avg=%.3fms] " +
                "request_time_aggregation[p50=%.3fms,p95=%.3fms,avg=%.3fms]%n",
                percentile(currentMs, 0.50), percentile(currentMs, 0.95), average(currentMs),
                percentile(aggregateMs, 0.50), percentile(aggregateMs, 0.95), average(aggregateMs)
        );
    }

    private void seed() {
        jdbcTemplate.update("DELETE FROM post_rankings");
        jdbcTemplate.update("DELETE FROM post_view_events");
        jdbcTemplate.update("DELETE FROM likes");
        jdbcTemplate.update("DELETE FROM posts");
        jdbcTemplate.update("DELETE FROM users");

        LocalDateTime now = LocalDateTime.now();

        jdbcTemplate.batchUpdate(
                "INSERT INTO users(email,password,nickname,profile_image,deleted_at) VALUES (?,?,?,?,NULL)",
                java.util.stream.IntStream.rangeClosed(1, 100)
                        .mapToObj(i -> new Object[]{"u" + i + "@test.com", "pw", "user" + i, null})
                        .toList()
        );

        List<Object[]> posts = new ArrayList<>(50000);
        for (int i = 1; i <= 10000; i++) {
            posts.add(new Object[]{
                    ((i - 1) % 100) + 1L,
                    "title-" + i,
                    "content-" + i,
                    null,
                    0L, 0L, 0L, false,
                    Timestamp.valueOf(now.minusMinutes(i % 10000)),
                    null, null
            });
        }
        jdbcTemplate.batchUpdate(
                "INSERT INTO posts(author_id,title,content,thumbnail_url,view_count,comment_count,like_count,blinded,created_at,updated_at,deleted_at) VALUES (?,?,?,?,?,?,?,?,?,?,?)",
                posts
        );

        List<Object[]> likes = new ArrayList<>(10000);
        for (int p = 1; p <= 10000; p++) {
            for (int k = 0; k < 5; k++) {
                long userId = ((p + k) % 100) + 1L;
                likes.add(new Object[]{p, userId, Timestamp.valueOf(now.minusDays(p % 5))});
            }
        }
        jdbcTemplate.batchUpdate(
                "INSERT INTO likes(post_id,user_id,created_at) VALUES (?,?,?)",
                likes
        );

        List<Object[]> views = new ArrayList<>(100000);
        for (int i = 1; i <= 100000; i++) {
            long postId = ((i - 1) % 10000) + 1L;
            long userId = ((i * 7L) % 100) + 1L;
            views.add(new Object[]{postId, userId, Timestamp.valueOf(now.minusDays(i % 7))});
        }
        jdbcTemplate.batchUpdate(
                "INSERT INTO post_view_events(post_id,user_id,viewed_at) VALUES (?,?,?)",
                views
        );
    }

    private void buildRankings() {
        jdbcTemplate.update("DELETE FROM post_rankings WHERE period_type='WEEKLY'");
        jdbcTemplate.update("""
                INSERT INTO post_rankings(period_type,post_id,like_count,view_count,score,rank_position,ranked_at)
                SELECT 'WEEKLY', ranked.post_id, ranked.like_count, ranked.view_count,
                       ranked.score, ranked.rank_position, NOW()
                FROM (
                    SELECT base.post_id, base.like_count, base.view_count, base.score,
                           ROW_NUMBER() OVER (ORDER BY base.score DESC, base.created_at DESC) AS rank_position
                    FROM (
                        SELECT p.post_id, p.created_at,
                               COALESCE(l.like_count,0) AS like_count,
                               COALESCE(v.view_count,0) AS view_count,
                               COALESCE(l.like_count,0) * 10 + COALESCE(v.view_count,0) AS score
                        FROM posts p
                        LEFT JOIN (
                            SELECT post_id, COUNT(*) like_count
                            FROM likes
                            WHERE created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
                            GROUP BY post_id
                        ) l ON l.post_id = p.post_id
                        LEFT JOIN (
                            SELECT post_id, COUNT(*) view_count
                            FROM post_view_events
                            WHERE viewed_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
                            GROUP BY post_id
                        ) v ON v.post_id = p.post_id
                        WHERE p.deleted_at IS NULL AND p.blinded = false
                    ) base
                ) ranked
                """);
    }

    private void currentLookup() {
        postService.getRankPost(PageRequest.of(0, 5), RankingPeriod.WEEKLY).getContent();
    }

    private void oldStyleAggregation() {
        jdbcTemplate.queryForList("""
                SELECT p.post_id,
                       COALESCE(l.like_count,0) AS like_count,
                       COALESCE(v.view_count,0) AS view_count,
                       COALESCE(l.like_count,0) * 10 + COALESCE(v.view_count,0) AS score
                FROM posts p
                LEFT JOIN (
                    SELECT post_id, COUNT(*) like_count
                    FROM likes
                    WHERE created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
                    GROUP BY post_id
                ) l ON l.post_id = p.post_id
                LEFT JOIN (
                    SELECT post_id, COUNT(*) view_count
                    FROM post_view_events
                    WHERE viewed_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
                    GROUP BY post_id
                ) v ON v.post_id = p.post_id
                WHERE p.deleted_at IS NULL AND p.blinded = false
                ORDER BY score DESC, p.created_at DESC
                LIMIT 5
                """);
    }

    private List<Double> measure(int count, Runnable task) {
        List<Double> values = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            long start = System.nanoTime();
            task.run();
            values.add((System.nanoTime() - start) / 1_000_000.0);
        }
        return values;
    }

    private double percentile(List<Double> values, double p) {
        List<Double> copy = new ArrayList<>(values);
        Collections.sort(copy);
        int index = Math.min(copy.size() - 1, (int) Math.ceil(copy.size() * p) - 1);
        return copy.get(index);
    }

    private double average(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
}
