package foodmap.V2.config;

import foodmap.V2.event.listener.NotificationEventListener;
import foodmap.V2.websocket.manager.NotificationSessionManager;
import foodmap.V2.websocket.manager.WebChatSessionManager;
import foodmap.V2.websocket.handler.ChatWebSocketHandler;
import foodmap.V2.websocket.repository.ChatRepository;
import foodmap.V2.user.repository.UserRepository;
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
    private final WebChatSessionManager webChatSessionManager;
    private final NotificationSessionManager notificationSessionManager;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ChatWebSocketHandler(userRepository,chatRepository,webChatSessionManager),"/webchat").addInterceptors(new HttpSessionHandshakeInterceptor())
                .addHandler(new NotificationEventListener(notificationSessionManager),"/notification").addInterceptors(new HttpSessionHandshakeInterceptor())
                .setAllowedOrigins("http://localhost:9030");
    }
}
