package kr.adapterz.springboot.chat.controller;

import jakarta.validation.Valid;
import kr.adapterz.springboot.chat.dto.ChatMessageRequest;
import kr.adapterz.springboot.chat.dto.ChatMessageResponse;
import kr.adapterz.springboot.chat.dto.ChatMessageRequest;
import kr.adapterz.springboot.chat.service.ChatMessageService;
import kr.adapterz.springboot.global.security.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/posts/{postId}/chat/messages")
    public void sendMessage(
            @DestinationVariable Long postId,
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
            @Valid ChatMessageRequest request
    ) {
        /*
         * 1. 메시지를 DB에 저장한다.
         * 2. 저장에 성공한 결과만 구독자에게 방송한다.
         */
        ChatMessageResponse response =
                chatMessageService.sendMessage(
                        postId,
                        customUserPrincipal.getUserId(),
                        request
                );

        String destination =
                "/topic/posts/" + postId + "/chat";

        messagingTemplate.convertAndSend(
                destination,
                response
        );
    }
}