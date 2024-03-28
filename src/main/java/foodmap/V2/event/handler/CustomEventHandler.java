package foodmap.V2.event.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import foodmap.V2.websocket.dto.response.EventNotificationResponseDTO;
import foodmap.V2.event.listener.NotificationEventListener;
import foodmap.V2.websocket.manager.NotificationSessionManager;
import foodmap.V2.websocket.domain.Notification;
import foodmap.V2.user.domain.UserInfo;
import foodmap.V2.websocket.dto.response.NotificationResponseDTO;
import foodmap.V2.exception.user.UserNotFound;
import foodmap.V2.websocket.repository.NotificationRepository;
import foodmap.V2.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomEventHandler {
    private final NotificationRepository notificationRepository;
    private final NotificationSessionManager notificationSessionManager;
    private final NotificationEventListener notificationEventListener;
    private final UserRepository userRepository;
    @EventListener
    public void handleCustomEvent(EventNotificationResponseDTO event) throws Exception {
        UserInfo sender = userRepository.findById(event.getSenderId()).orElseThrow(UserNotFound::new);
        String senderName = sender.getUsername();
        String notificationMessage = String.format("%s님이 회원님의 %s 게시글에 좋아요를 눌렀습니다.", senderName,event.getPost().getTitle());
        Notification notification = Notification.builder()
                .message(notificationMessage)
                .is_read(false)
                .reciever(event.getReceiverId())
                .created_at(String.valueOf(LocalDateTime.now()))
                .build();
        Notification savedNotification = notificationRepository.save(notification);
        NotificationResponseDTO notificationResponseDTO = NotificationResponseDTO.builder()
                .not(savedNotification).build();
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        TextMessage textMessage = new TextMessage(objectMapper.writeValueAsString(notificationResponseDTO));
        for (WebSocketSession s : notificationSessionManager.getSessionList()) {
            String url = String.valueOf(s.getUri());
            Long uid = Long.valueOf(url.split("=")[1]);
            if (uid.equals(event.getReceiverId())) {
                notificationEventListener.handleMessage(s, textMessage);
            }
        }
    }
}
