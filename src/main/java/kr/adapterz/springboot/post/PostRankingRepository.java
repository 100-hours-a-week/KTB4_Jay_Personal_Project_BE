package kr.adapterz.springboot.post;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface PostRankingRepository extends JpaRepository<PostRanking, Long> {
    void deleteByPeriodType(RankingPeriod periodType);

    @Query(
            value = """
                    select r
                    from PostRanking r
                    join fetch r.post p
                    join fetch p.author
                    where r.periodType = :periodType
                    order by r.rankPosition asc
                    """,
            countQuery = """
                    select count(r)
                    from PostRanking r
                    where r.periodType = :periodType
                    """
    )
    Page<PostRanking> findRankingsWithPostAndAuthor(
            @Param("periodType") RankingPeriod periodType,
            Pageable pageable
    );

    @Modifying
    @Query(value = """
            INSERT INTO post_rankings (
                period_type,
                post_id,
                like_count,
                view_count,
                score,
                rank_position,
                ranked_at
            )
            SELECT
                :periodType AS period_type,
                ranked.post_id,
                ranked.like_count,
                ranked.view_count,
                ranked.score,
                ranked.rank_position,
                NOW() AS ranked_at
            FROM (
                SELECT
                    base.post_id,
                    base.like_count,
                    base.view_count,
                    base.score,
                    ROW_NUMBER() OVER (
                        ORDER BY base.score DESC, base.created_at DESC
                    ) AS rank_position
                FROM (
                    SELECT
                        p.post_id,
                        p.created_at,
                        COALESCE(l.like_count, 0) AS like_count,
                        COALESCE(v.view_count, 0) AS view_count,
                        (COALESCE(l.like_count, 0) * 10 + COALESCE(v.view_count, 0)) AS score
                    FROM posts p
                    LEFT JOIN (
                        SELECT post_id, COUNT(*) AS like_count
                        FROM likes
                        WHERE created_at >= :startTime
                        GROUP BY post_id
                    ) l ON l.post_id = p.post_id
                    LEFT JOIN (
                        SELECT post_id, COUNT(*) AS view_count
                        FROM post_view_events
                        WHERE viewed_at >= :startTime
                        GROUP BY post_id
                    ) v ON v.post_id = p.post_id
                    WHERE p.deleted_at IS NULL
                      AND p.blinded = false
                ) base
            ) ranked
            """, nativeQuery = true)
    int insertRankings(
            @Param("periodType") String periodType,
            @Param("startTime") LocalDateTime startTime
    );
}
