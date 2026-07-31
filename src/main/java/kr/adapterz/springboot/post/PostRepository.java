package kr.adapterz.springboot.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query(
            value = """
                    select p
                    from Post p
                    join fetch p.author
                    where p.deletedAt is null
                    """,
                // 페이지네이션 하기 위해 전체 게시글 개수세기
            countQuery = """
                    select count(p)
                    from Post p
                    where p.deletedAt is null
                    """
    )
    Page<Post> findAllWithAuthorFetchJoin(Pageable pageable);

    @Query("""
            select p
            from Post p
            join fetch p.author
            where p.id = :postId
            """)
    Optional<Post> findByIdWithAuthor(@Param("postId") Long postId);

    @Query(
            value = """
            SELECT
                p.post_id AS postId,
                p.title AS title,
                u.nickname AS authorNickname,
                CASE WHEN u.deleted_at IS NULL THEN false ELSE true END AS authorDeleted,
                p.like_count AS likeCount,
                p.comment_count AS commentCount,
                p.view_count AS viewCount,
                p.created_at AS createdAt,
                p.updated_at AS updatedAt,
                COALESCE(l.like_count, 0) AS periodLikeCount,
                COALESCE(v.view_count, 0) AS periodViewCount
            FROM posts p
            JOIN users u ON u.user_id = p.author_id
            LEFT JOIN (
                SELECT post_id, COUNT(*) AS like_count
                FROM likes
                WHERE created_at >= :startTime
                GROUP BY post_id
            ) l ON l.post_id = p.post_id
            LEFT JOIN (
                SELECT post_id, COUNT(*) AS view_count
                FROM post_views
                WHERE viewed_at >= :startTime
                GROUP BY post_id
            ) v ON v.post_id = p.post_id
            WHERE p.deleted_at IS NULL
              AND p.blinded = false
            ORDER BY
                COALESCE(l.like_count, 0) DESC,
                COALESCE(v.view_count, 0) DESC,
                p.created_at DESC
            """,
            countQuery = """
            SELECT COUNT(*)
            FROM posts p
            WHERE p.deleted_at IS NULL
              AND p.blinded = false
            """,
            nativeQuery = true
    )
    Page<RankedPostProjection> findRankPosts(@Param("startTime") LocalDateTime startTime, Pageable pageable);
}
