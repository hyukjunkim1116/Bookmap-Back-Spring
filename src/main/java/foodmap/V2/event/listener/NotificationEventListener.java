package foodmap.V2.event.listener;
import foodmap.V2.websocket.manager.NotificationSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationEventListener implements WebSocketHandler {
    private final NotificationSessionManager notificationSessionManager;
    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        notificationSessionManager.addSession(session);
    }
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        session.sendMessage(message);
    }
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception){
        notificationSessionManager.removeSession(session);
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus){
        notificationSessionManager.removeSession(session);
    }
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}

