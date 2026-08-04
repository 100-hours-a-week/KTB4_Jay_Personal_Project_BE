package kr.adapterz.springboot.chat.dto;

import kr.adapterz.springboot.chat.entity.ChatMessage;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long messageId,
        Long postId,
        Long senderId,
        String senderNickname,
        String content,
        LocalDateTime createdAt
) {

    public static ChatMessageResponse from(
            ChatMessage chatMessage
    ) {
        return new ChatMessageResponse(
                chatMessage.getId(),
                chatMessage.getPost().getId(),
                chatMessage.getSender().getId(),
                chatMessage.getSender().getNickname(),
                chatMessage.getContent(),
                chatMessage.getCreatedAt()
        );
    }
}