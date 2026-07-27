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
            select p
            from Post p
            join fetch p.author
            left join Like l
                on l.post = p
                and l.createdAt >= :startTime
            left join PostView pv
                on pv.post = p
                and pv.viewedAt >= :startTime
            where p.deletedAt is null
              and p.blinded = false
            group by p
            order by count(distinct l.id) desc,
                     count(distinct pv.id) desc,
                     p.createdAt desc
            """,
            countQuery = """
            select count(p)
            from Post p
            where p.deletedAt is null
              and p.blinded = false
            """
    )
    Page<Post> findRankPosts(@Param("startTime") LocalDateTime startTime, Pageable pageable);
}