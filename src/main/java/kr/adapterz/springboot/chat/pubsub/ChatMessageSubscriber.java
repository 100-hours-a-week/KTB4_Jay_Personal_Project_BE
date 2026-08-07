package kr.adapterz.springboot.chat.pubsub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.adapterz.springboot.chat.dto.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatMessageSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onMessage(
            Message message,
            byte[] pattern
    ) {
        try {
            String body =
                    new String(message.getBody());

            ChatMessageResponse response =
                    objectMapper.readValue(
                            body,
                            ChatMessageResponse.class
                    );

            String destination =
                    "/topic/posts/" + response.postId() + "/chat";

            messagingTemplate.convertAndSend(
                    destination,
                    response
            );
        } catch (JsonProcessingException e) {
            log.error(
                    "채팅 메시지 구독 처리에 실패했습니다.",
                    e
            );
        }
    }
}