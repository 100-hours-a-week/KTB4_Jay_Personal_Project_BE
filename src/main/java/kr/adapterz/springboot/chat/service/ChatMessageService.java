package kr.adapterz.springboot.chat.service;

import kr.adapterz.springboot.chat.dto.ChatMessageResponse;
import kr.adapterz.springboot.chat.dto.ChatMessageRequest;
import kr.adapterz.springboot.chat.entity.ChatMessage;
import kr.adapterz.springboot.chat.repository.ChatMessageRepository;
import kr.adapterz.springboot.post.Post;
import kr.adapterz.springboot.post.PostRepository;
import kr.adapterz.springboot.user.User;
import kr.adapterz.springboot.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatMessageResponse sendMessage(
            Long postId,
            Long currentUserId,
            ChatMessageRequest request
    ) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "존재하지 않는 게시글입니다."
                        )
                );

        User sender = userRepository.findById(currentUserId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "존재하지 않는 사용자입니다."
                        )
                );

        String content = request.content().trim();

        if (content.isEmpty()) {
            throw new IllegalArgumentException(
                    "메시지는 비어 있을 수 없습니다."
            );
        }

        ChatMessage chatMessage = new ChatMessage(
                post,
                sender,
                content,
                LocalDateTime.now()
        );

        ChatMessage savedMessage =
                chatMessageRepository.save(chatMessage);

        log.info("Chat message persisted messageId={} postId={} senderId={}",
                savedMessage.getId(),
                post.getId(),
                sender.getId()
        );

        return ChatMessageResponse.from(savedMessage);
    }
}
