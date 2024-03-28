package foodmap.V2.config;

import foodmap.V2.event.listener.NotificationEventListener;
import foodmap.V2.websocket.manager.WebSocketSessionManager;
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
    private final WebSocketSessionManager sessionManager;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ChatWebSocketHandler(userRepository,chatRepository,sessionManager),"/webchat").addInterceptors(new HttpSessionHandshakeInterceptor())
                .addHandler(new NotificationEventListener(sessionManager),"/notification").addInterceptors(new HttpSessionHandshakeInterceptor())
                .setAllowedOrigins("http://localhost:9030");
    }
}
