USE bamboo;

EXPLAIN ANALYZE
SELECT
    p.post_id,
    p.title,
    u.nickname AS authorNickname,
    CASE WHEN u.deleted_at IS NULL THEN false ELSE true END AS authorDeleted,
    p.like_count,
    p.comment_count,
    p.view_count,
    p.created_at,
    p.updated_at,
    COALESCE(l.like_count, 0) AS periodLikeCount,
    COALESCE(v.view_count, 0) AS periodViewCount
FROM posts p
         JOIN users u ON u.user_id = p.author_id
         LEFT JOIN (
    SELECT post_id, COUNT(*) AS like_count
    FROM likes
    WHERE created_at >= '2026-07-20 00:00:00'
    GROUP BY post_id
) l ON l.post_id = p.post_id
         LEFT JOIN (
    SELECT post_id, COUNT(*) AS view_count
    FROM post_views
    WHERE viewed_at >= '2026-07-20 00:00:00'
    GROUP BY post_id
) v ON v.post_id = p.post_id
WHERE p.deleted_at IS NULL
  AND p.blinded = false
ORDER BY
    COALESCE(l.like_count, 0) DESC,
    COALESCE(v.view_count, 0) DESC,
    p.created_at DESC
    LIMIT 5;