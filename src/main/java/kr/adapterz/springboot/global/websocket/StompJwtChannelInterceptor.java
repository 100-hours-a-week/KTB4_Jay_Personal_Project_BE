package kr.adapterz.springboot.global.websocket;

import kr.adapterz.springboot.global.security.CustomUserPrincipal;
import kr.adapterz.springboot.global.security.JwtTokenProvider;
import kr.adapterz.springboot.user.User;
import kr.adapterz.springboot.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompJwtChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            return message;
        }

        StompCommand command = accessor.getCommand();

        if (StompCommand.CONNECT.equals(command)) {
            log.info("STOMP CONNECT received");
            authenticateConnect(accessor);
        }

        if (StompCommand.SEND.equals(command)) {
            log.info("STOMP SEND received destination={} user={}",
                    accessor.getDestination(),
                    accessor.getUser()
            );
            requireAuthenticatedUser(accessor);
        }

        return message;
    }

    private void authenticateConnect(StompHeaderAccessor accessor) {
        String authorization = getAuthorizationHeader(accessor);

        if (authorization == null || authorization.isBlank()) {
            log.warn("STOMP CONNECT without Authorization header");
            return;
        }

        if (!authorization.startsWith("Bearer ")) {
            throw new MessagingException("invalid_access_token");
        }

        String token = authorization.substring(7);

        if (jwtTokenProvider.isExpiredToken(token)) {
            throw new MessagingException("expired_access_token");
        }

        if (!jwtTokenProvider.validateToken(token)) {
            throw new MessagingException("invalid_access_token");
        }

        if (!"access".equals(jwtTokenProvider.getTokenType(token))) {
            throw new MessagingException("invalid_access_token");
        }

        Long userId = jwtTokenProvider.getUserId(token);
        User user = userRepository.findById(userId)
                .filter(foundUser -> !foundUser.isDeleted())
                .orElseThrow(() -> new MessagingException("invalid_access_token"));

        CustomUserPrincipal principal = new CustomUserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getNickname()
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        accessor.setUser(authentication);
        log.info("STOMP authenticated userId={}", user.getId());
    }

    private String getAuthorizationHeader(StompHeaderAccessor accessor) {
        String authorization = accessor.getFirstNativeHeader("Authorization");

        if (authorization == null) {
            authorization = accessor.getFirstNativeHeader("authorization");
        }

        return authorization;
    }

    private void requireAuthenticatedUser(StompHeaderAccessor accessor) {
        if (accessor.getUser() == null) {
            log.warn("STOMP SEND blocked because user is not authenticated destination={}", accessor.getDestination());
            throw new MessagingException("authentication_required");
        }
    }
}
