package kr.adapterz.springboot.post;

import jakarta.persistence.EntityManager;
import kr.adapterz.springboot.like.Like;
import kr.adapterz.springboot.like.LikeRepository;
import kr.adapterz.springboot.post.entity.Post;
import kr.adapterz.springboot.post.entity.PostRanking;
import kr.adapterz.springboot.post.entity.PostViewEvents;
import kr.adapterz.springboot.post.repository.PostRankingRepository;
import kr.adapterz.springboot.post.repository.PostRepository;
import kr.adapterz.springboot.post.repository.PostViewEventsRepository;
import kr.adapterz.springboot.post.service.RankingBatchService;
import kr.adapterz.springboot.user.User;
import kr.adapterz.springboot.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class RankingBatchServiceIntegrationTest {

    @Autowired
    private RankingBatchService rankingBatchService;

    @Autowired
    private PostRankingRepository postRankingRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostViewEventsRepository postViewEventsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(new User("ranking@test.com", "encoded-password", "랭킹유저", null));
    }

    @Test
    void 배치를_실행하면_점수순으로_랭킹이_저장된다() {
        Post likePost = postRepository.save(new Post(user, "좋아요가 더 많은 글", "내용"));
        Post viewPost = postRepository.save(new Post(user, "조회수가 점수를 뒤집는 글", "내용"));

        addLikes(likePost, 2, LocalDateTime.now());
        addLikes(viewPost, 1, LocalDateTime.now());
        addViewEvents(viewPost, 15, LocalDateTime.now());

        rankingBatchService.refreshRanking(RankingPeriod.DAILY);

        Page<PostRanking> rankings = findDailyRankings();

        assertThat(rankings.getContent())
                .extracting(ranking -> ranking.getPost().getId())
                .containsExactly(viewPost.getId(), likePost.getId());
        assertThat(rankings.getContent().get(0).getScore()).isEqualTo(25L);
        assertThat(rankings.getContent().get(0).getRankPosition()).isEqualTo(1);
        assertThat(rankings.getContent().get(1).getScore()).isEqualTo(20L);
        assertThat(rankings.getContent().get(1).getRankPosition()).isEqualTo(2);
    }

    @Test
    void 배치는_기간_밖_좋아요와_조회수를_제외한다() {
        Post oldPost = postRepository.save(new Post(user, "어제 인기글", "내용"));
        Post todayPost = postRepository.save(new Post(user, "오늘 인기글", "내용"));
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);

        addLikes(oldPost, 5, yesterday);
        addViewEvents(oldPost, 20, yesterday);
        addLikes(todayPost, 1, LocalDateTime.now());

        rankingBatchService.refreshRanking(RankingPeriod.DAILY);

        PostRanking first = findDailyRankings().getContent().getFirst();

        assertThat(first.getPost().getId()).isEqualTo(todayPost.getId());
        assertThat(first.getLikeCount()).isEqualTo(1L);
        assertThat(first.getViewCount()).isEqualTo(0L);
        assertThat(first.getScore()).isEqualTo(10L);
    }

    @Test
    void 배치를_다시_실행해도_같은_기간_랭킹이_중복_저장되지_않는다() {
        Post firstPost = postRepository.save(new Post(user, "첫 번째 글", "내용"));
        Post secondPost = postRepository.save(new Post(user, "두 번째 글", "내용"));

        addLikes(firstPost, 2, LocalDateTime.now());
        addLikes(secondPost, 1, LocalDateTime.now());

        rankingBatchService.refreshRanking(RankingPeriod.DAILY);
        rankingBatchService.refreshRanking(RankingPeriod.DAILY);

        Page<PostRanking> rankings = findDailyRankings();

        assertThat(rankings.getTotalElements()).isEqualTo(2L);
        assertThat(rankings.getContent())
                .extracting(ranking -> ranking.getPost().getId())
                .containsExactly(firstPost.getId(), secondPost.getId());
    }

    private Page<PostRanking> findDailyRankings() {
        return postRankingRepository.findRankingsWithPostAndAuthor(
                RankingPeriod.DAILY,
                PageRequest.of(0, 10)
        );
    }

    private void addLikes(Post post, int count, LocalDateTime createdAt) {
        for (int i = 0; i < count; i++) {
            User likeUser = createUser("batch-like-" + post.getId() + "-" + i);
            likeRepository.save(new Like(post, likeUser, createdAt));
        }
        flushAndClear();
    }

    private void addViewEvents(Post post, int count, LocalDateTime viewedAt) {
        for (int i = 0; i < count; i++) {
            User viewUser = createUser("batch-view-" + post.getId() + "-" + i);
            postViewEventsRepository.save(new PostViewEvents(post, viewUser, viewedAt));
        }
        flushAndClear();
    }

    private User createUser(String key) {
        return userRepository.save(new User(key + "@test.com", "encoded-password", key, null));
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
