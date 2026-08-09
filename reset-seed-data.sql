SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE chat_messages;
TRUNCATE TABLE post_rankings;
TRUNCATE TABLE post_view_events;
TRUNCATE TABLE post_views;
TRUNCATE TABLE likes;
TRUNCATE TABLE comments;
TRUNCATE TABLE report;
TRUNCATE TABLE post_draft;
TRUNCATE TABLE refresh_token;
TRUNCATE TABLE posts;
TRUNCATE TABLE users;

SET FOREIGN_KEY_CHECKS = 1;

SELECT 'users' AS table_name, COUNT(*) AS count FROM users
UNION ALL
SELECT 'posts', COUNT(*) FROM posts
UNION ALL
SELECT 'likes', COUNT(*) FROM likes
UNION ALL
SELECT 'comments', COUNT(*) FROM comments
UNION ALL
SELECT 'post_views', COUNT(*) FROM post_views
UNION ALL
SELECT 'post_view_events', COUNT(*) FROM post_view_events
UNION ALL
SELECT 'post_rankings', COUNT(*) FROM post_rankings;