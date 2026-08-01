package kr.adapterz.springboot.post;

import jakarta.persistence.EntityManager;
import kr.adapterz.springboot.like.Like;
import kr.adapterz.springboot.like.LikeRepository;
import kr.adapterz.springboot.post.dto.PostDetailResponse;
import kr.adapterz.springboot.post.dto.PostListResponse;
import kr.adapterz.springboot.post.dto.PostRequest;
import kr.adapterz.springboot.post.dto.PostResponse;
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
class PostServiceIntegrationTest {

    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostViewEventsRepository postViewEventsRepository;

    @Autowired
    private EntityManager entityManager;

    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(new User("post@test.com", "encoded-password", "게시글작성자", null));
    }

    // ==============================
    // 게시글 작성 통합 흐름
    // ==============================

    @Test
    void 게시글_작성하면_DB에_저장된다() {
        PostResponse response = postService.createPost(
                user.getId(),
                postRequest("통합 제목", "통합 내용")
        );

        Post savedPost = postRepository.findById(response.getPostId())
                .orElseThrow();

        assertThat(savedPost.getTitle()).isEqualTo("통합 제목");
        assertThat(savedPost.getContent()).isEqualTo("통합 내용");
        assertThat(savedPost.getAuthor().getId()).isEqualTo(user.getId());
    }

    // ==============================
    // 조회수 24시간 중복 방지
    // ==============================

    @Test
    void 같은_사용자가_24시간_안에_다시_조회하면_조회수가_증가하지_않는다() {
        PostResponse created = postService.createPost(
                user.getId(),
                postRequest("조회수 제목", "조회수 내용")
        );

        postService.getPostDetail(created.getPostId(), user.getId());
        PostDetailResponse second = postService.getPostDetail(created.getPostId(), user.getId());

        assertThat(second.getViewCount()).isEqualTo(1L);
    }

    // ==============================
    // 인기글 기간별 랭킹
    // ==============================

    @Test
    void 인기글은_좋아요와_조회수를_점수로_계산해서_정렬한다() {
        Post likePost = postRepository.save(new Post(user, "좋아요가 더 많은 글", "내용"));
        Post viewPost = postRepository.save(new Post(user, "조회수가 점수를 뒤집는 글", "내용"));

        addLikes(likePost, 2, LocalDateTime.now());
        addLikes(viewPost, 1, LocalDateTime.now());
        addViewEvents(viewPost, 15, LocalDateTime.now());

        Page<PostListResponse> result = postService.getRankPost(PageRequest.of(0, 5), RankingPeriod.DAILY);

        assertThat(result.getContent())
                .extracting(PostListResponse::getPostId)
                .containsSubsequence(viewPost.getId(), likePost.getId());
    }

    @Test
    void 인기글은_기간_밖_좋아요와_조회수를_제외한다() {
        Post oldPost = postRepository.save(new Post(user, "어제 인기글", "내용"));
        Post todayPost = postRepository.save(new Post(user, "오늘 인기글", "내용"));
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);

        addLikes(oldPost, 5, yesterday);
        addViewEvents(oldPost, 20, yesterday);
        addLikes(todayPost, 1, LocalDateTime.now());

        Page<PostListResponse> result = postService.getRankPost(PageRequest.of(0, 5), RankingPeriod.DAILY);

        assertThat(result.getContent().getFirst().getPostId()).isEqualTo(todayPost.getId());
        assertThat(result.getContent().getFirst().getLikeCount()).isEqualTo(1L);
        assertThat(result.getContent().getFirst().getViewCount()).isEqualTo(0L);
    }

    @Test
    void 인기글_주간_기준은_월요일_0시부터다() {
        Post lastWeekPost = postRepository.save(new Post(user, "지난주 글", "내용"));
        Post thisWeekPost = postRepository.save(new Post(user, "이번주 글", "내용"));
        LocalDateTime weekStart = RankingPeriod.WEEKLY.getStartDateTime();

        addLikes(lastWeekPost, 5, weekStart.minusSeconds(1));
        addLikes(thisWeekPost, 1, weekStart);

        Page<PostListResponse> result = postService.getRankPost(PageRequest.of(0, 5), RankingPeriod.WEEKLY);

        assertThat(result.getContent().getFirst().getPostId()).isEqualTo(thisWeekPost.getId());
        assertThat(result.getContent().getFirst().getLikeCount()).isEqualTo(1L);
    }

    private PostRequest postRequest(String title, String content) {
        return new PostRequest(title, content);
    }

    private void addLikes(Post post, int count, LocalDateTime createdAt) {
        for (int i = 0; i < count; i++) {
            User likeUser = createUser("like-" + post.getId() + "-" + i);
            likeRepository.save(new Like(post, likeUser, createdAt));
        }
        flushAndClear();
    }

    private void addViewEvents(Post post, int count, LocalDateTime viewedAt) {
        for (int i = 0; i < count; i++) {
            User viewUser = createUser("view-" + post.getId() + "-" + i);
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
