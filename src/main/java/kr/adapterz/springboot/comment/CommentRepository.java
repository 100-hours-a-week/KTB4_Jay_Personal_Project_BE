package kr.adapterz.springboot.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("""
        select c
        from Comment c
        join fetch c.author
        where c.post.id = :postId
          and c.parentComment is null
        order by c.createdAt asc
    """)
    List<Comment> findParentCommentsByPostIdWithAuthor(@Param("postId") Long postId);


    @Query("""
    select c
    from Comment c
    join fetch c.author
    where c.parentComment.id = :parentCommentId
    order by c.createdAt asc
""")
    List<Comment> findRepliesByParentCommentIdWithAuthor(@Param("parentCommentId") Long parentCommentId);


}
