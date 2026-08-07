package kr.adapterz.springboot.chat.pubsub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.adapterz.springboot.chat.dto.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatMessagePublisher {

    private static final String CHAT_MESSAGE_CHANNEL =
            "chat:messages";

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public void publish(
            ChatMessageResponse response
    ) {
        try {
            String message =
                    objectMapper.writeValueAsString(response);

            stringRedisTemplate.convertAndSend(
                    CHAT_MESSAGE_CHANNEL,
                    message
            );
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "채팅 메시지 발행에 실패했습니다.",
                    e
            );
        }
    }
}