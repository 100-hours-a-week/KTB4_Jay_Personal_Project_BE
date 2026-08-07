package kr.adapterz.springboot.post.repository;

import kr.adapterz.springboot.post.entity.PostView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostViewRepository extends JpaRepository<PostView, Long> {

    Optional<PostView> findByPost_IdAndUser_Id(Long postId, long userId);
}
