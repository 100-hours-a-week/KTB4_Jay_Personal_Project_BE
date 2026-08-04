package kr.adapterz.springboot.chat.repository;

import kr.adapterz.springboot.chat.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository
        extends JpaRepository<ChatMessage, Long> {

    /*
     * 과거 메시지를 조회할 때 sender를 함께 조회해
     * 닉네임 접근 시 N+1이 생기는 것을 줄인다.
     */
    @EntityGraph(attributePaths = {"sender"})
    Page<ChatMessage> findByPostId(
            Long postId,
            Pageable pageable
    );
}