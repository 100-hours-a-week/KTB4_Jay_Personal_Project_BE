USE bamboo;

EXPLAIN ANALYZE
SELECT
    p.post_id,
    COUNT(DISTINCT l.like_id) AS period_like_count,
    COUNT(DISTINCT pve.post_view_events_id) AS period_view_count
FROM posts p
         LEFT JOIN likes l
                   ON l.post_id = p.post_id
                       AND l.created_at >= '2026-07-20 00:00:00'
         LEFT JOIN post_view_events pve
                   ON pve.post_id = p.post_id
                       AND pve.viewed_at >= '2026-07-20 00:00:00'
WHERE p.deleted_at IS NULL
  AND p.blinded = false
GROUP BY p.post_id
ORDER BY
    period_like_count DESC,
    period_view_count DESC,
    p.created_at DESC
    LIMIT 5;
