package foodmap.V2.config.listener;
import foodmap.V2.config.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationEventListener implements WebSocketHandler {
    private final WebSocketSessionManager webSocketSessionManager;
    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        webSocketSessionManager.addSession(session);
        log.info("sessionList,{}",webSocketSessionManager.getSessionList());
        log.info("NotificationWebSocketHandler 연결완료");
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        session.sendMessage(message);
        log.info("send");
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception){
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus){
        webSocketSessionManager.removeSession(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}

