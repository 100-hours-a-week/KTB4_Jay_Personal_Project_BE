package kr.adapterz.springboot.chat.controller;

import jakarta.validation.Valid;
import kr.adapterz.springboot.chat.dto.ChatMessageRequest;
import kr.adapterz.springboot.chat.dto.ChatMessageResponse;
import kr.adapterz.springboot.chat.service.ChatMessageService;
import kr.adapterz.springboot.global.security.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/posts/{postId}/chat/messages")
    public void sendMessage(
            @DestinationVariable Long postId,
            Principal principal,
            @Payload @Valid ChatMessageRequest request
    ) {
        log.info("Chat message request received postId={} principal={}", postId, principal);

        /*
         * 1. 메시지를 DB에 저장한다.
         * 2. 저장에 성공한 결과만 구독자에게 방송한다.
         */
        ChatMessageResponse response =
                chatMessageService.sendMessage(
                        postId,
                        getCurrentUserId(principal),
                        request
                );

        String destination =
                "/topic/posts/" + postId + "/chat";

        log.info("Chat message saved messageId={} postId={}", response.messageId(), postId);

        messagingTemplate.convertAndSend(
                destination,
                response
        );
    }

    private Long getCurrentUserId(Principal principal) {
        if (!(principal instanceof Authentication authentication)) {
            throw new MessagingException("authentication_required");
        }

        CustomUserPrincipal customUserPrincipal =
                (CustomUserPrincipal) authentication.getPrincipal();

        return customUserPrincipal.getUserId();
    }
}
