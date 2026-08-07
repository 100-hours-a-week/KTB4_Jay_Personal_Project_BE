package kr.adapterz.springboot.chat.service;

import kr.adapterz.springboot.chat.dto.ChatMessageResponse;
import kr.adapterz.springboot.chat.repository.ChatMessageRepository;
import kr.adapterz.springboot.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatMessageQueryService {

    private final ChatMessageRepository chatMessageRepository;
    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public Page<ChatMessageResponse> getMessages(
            Long postId,
            Pageable pageable
    ) {
        if (!postRepository.existsById(postId)) {
            throw new IllegalArgumentException(
                    "존재하지 않는 게시글입니다."
            );
        }

        return chatMessageRepository
                .findByPostId(postId, pageable)
                .map(ChatMessageResponse::from);
    }
}