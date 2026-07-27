package kr.adapterz.springboot.postDraft;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostDraftRepository extends JpaRepository<PostDraft, Long> {

    Optional<PostDraft> findByUser_Id(Long userId);

}