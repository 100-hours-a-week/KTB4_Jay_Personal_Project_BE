USE bamboo;

EXPLAIN ANALYZE
SELECT
    r.ranking_id,
    r.period_type,
    r.post_id,
    r.like_count,
    r.view_count,
    r.score,
    r.rank_position,
    r.ranked_at,
    p.post_id,
    p.title,
    p.comment_count,
    p.created_at,
    p.updated_at,
    u.user_id,
    u.nickname,
    u.deleted_at
FROM post_rankings r
         JOIN posts p ON p.post_id = r.post_id
         JOIN users u ON u.user_id = p.author_id
WHERE r.period_type = 'WEEKLY'
  AND p.deleted_at IS NULL
  AND p.blinded = false
ORDER BY r.rank_position ASC
LIMIT 5;
