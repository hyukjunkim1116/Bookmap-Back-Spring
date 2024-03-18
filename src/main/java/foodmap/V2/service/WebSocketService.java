package foodmap.V2.service;
import foodmap.V2.domain.Notification;
import foodmap.V2.config.websocket.ChatResponse;
import foodmap.V2.dto.response.NotificationResponse;
import foodmap.V2.repository.ChatRepository;
import foodmap.V2.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketService {
    private final ChatRepository chatRepository;
    private final NotificationRepository notificationRepository;
    public List<ChatResponse> getChatList() {
        return chatRepository.findAll().stream().map(ChatResponse::new).collect(Collectors.toList());
    }

    public List<NotificationResponse> getNotificationList(Long userId) {
        return notificationRepository.findAllByReciever(userId).stream()
                    .map(NotificationResponse::new)
                    .collect(Collectors.toList());
    }
    public void setNotRead(Long userId, Long notId) {
        Optional<Notification> notificationOptional = notificationRepository.findById(notId);
        if (notificationOptional.isPresent()) {
            Notification notification = notificationOptional.get();
            notification.setIs_read();
            notificationRepository.save(notification);
        }

    }
}
