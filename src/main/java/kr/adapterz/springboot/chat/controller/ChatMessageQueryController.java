package kr.adapterz.springboot.chat.controller;

import kr.adapterz.springboot.chat.dto.ChatMessageResponse;
import kr.adapterz.springboot.chat.service.ChatMessageQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/posts/{postId}/chat/messages")
@RequiredArgsConstructor
public class ChatMessageQueryController {

    private final ChatMessageQueryService chatMessageQueryService;

    @GetMapping
    public Page<ChatMessageResponse> getMessages(
            @PathVariable Long postId,

            @PageableDefault(
                    size = 30,
                    sort = "createdAt",
                    direction = DESC
            )
            Pageable pageable
    ) {
        return chatMessageQueryService.getMessages(
                postId,
                pageable
        );
    }
}