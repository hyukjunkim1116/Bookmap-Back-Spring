package foodmap.V2.config.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import foodmap.V2.config.WebSocketSessionManager;
import foodmap.V2.domain.Chat;
import foodmap.V2.domain.UserInfo;
import foodmap.V2.dto.request.ChatRequestDTO;
import foodmap.V2.dto.response.ChatResponse;
import foodmap.V2.exception.user.UserNotFound;
import foodmap.V2.repository.ChatRepository;
import foodmap.V2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;
import java.time.LocalDateTime;

/**
 * 웹소켓 핸들러 - 세션 구분 X
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatWebSocketHandler implements WebSocketHandler {
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final WebSocketSessionManager webSocketSessionManager;


    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        webSocketSessionManager.addSession(session);
        log.info("ChatWebSocketHandler 연결완료");
        // Connection established
    }
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        // WebSocket 연결 요청에서 쿼리 매개변수 가져오기
        String url = String.valueOf(session.getUri());
        Long uid = Long.valueOf(url.split("=")[1]); // uid=1에서 1 추출
        UserInfo user = userRepository.findById(uid).orElseThrow(UserNotFound::new);
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        // 메시지 페이로드를 문자열로 변환
        String payload = (String) message.getPayload();
        // 문자열을 ChatRequestDTO 객체로 변환
        ChatRequestDTO chat = objectMapper.readValue(payload, ChatRequestDTO.class);
        Chat savedChat = Chat.builder()
                        .content(chat.getMessage())
                        .sender(user.getUsername())
                        .timestamp(LocalDateTime.now())
                        .sender_image(user.getImage())
                        .sender_id(user.getId())
                        .build();
        chatRepository.save(savedChat);
        ChatResponse chatResponse= ChatResponse.builder()
                .chat(savedChat)
                .build();
        log.info("session,{}",session.getAttributes());
        TextMessage textMessage = new TextMessage(objectMapper.writeValueAsString(chatResponse));
        for (WebSocketSession s : webSocketSessionManager.getSessionList()) {
            s.sendMessage(textMessage);
        }

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception){
        log.info("error occured");
        webSocketSessionManager.removeSession(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus){
        log.info("close chat");
        webSocketSessionManager.removeSession(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
