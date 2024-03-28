package foodmap.V2.websocket.manager;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import java.util.ArrayList;
import java.util.List;

@Getter
@Component
@RequiredArgsConstructor
public class WebChatSessionManager {
    private final List<WebSocketSession> sessionList = new ArrayList<>();
    public void addSession(WebSocketSession session) {
        sessionList.add(session);
    }
    public void removeSession(WebSocketSession session) {
        sessionList.remove(session);
    }
}
