package kr.adapterz.springboot.global.redis;

import kr.adapterz.springboot.chat.pubsub.ChatMessageSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
@Profile("!h2")
public class RedisConfig {

    private static final String CHAT_MESSAGE_CHANNEL =
            "chat:messages";

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory redisConnectionFactory,
            ChatMessageSubscriber chatMessageSubscriber
    ) {
        RedisMessageListenerContainer container =
                new RedisMessageListenerContainer();

        container.setConnectionFactory(redisConnectionFactory);

        container.addMessageListener(
                chatMessageSubscriber,
                new ChannelTopic(CHAT_MESSAGE_CHANNEL)
        );

        return container;
    }
}
