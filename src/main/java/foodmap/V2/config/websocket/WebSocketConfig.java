package foodmap.V2.config.websocket;

import foodmap.V2.config.event.NotificationEventListener;
import foodmap.V2.repository.ChatRepository;
import foodmap.V2.repository.NotificationRepository;
import foodmap.V2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;


@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final NotificationRepository notificationRepository;
    private final WebSocketSessionManager sessionManager;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ChatWebSocketHandler(userRepository,chatRepository),"/webchat").addInterceptors(new HttpSessionHandshakeInterceptor())
                .addHandler(new NotificationEventListener(sessionManager),"/notification").addInterceptors(new HttpSessionHandshakeInterceptor())
                .setAllowedOrigins("http://localhost:9030");
    }
}
