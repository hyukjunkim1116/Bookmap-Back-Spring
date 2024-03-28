package foodmap.V2.websocket.service;
import foodmap.V2.websocket.domain.Notification;
import foodmap.V2.websocket.dto.response.ChatResponseDTO;
import foodmap.V2.websocket.dto.response.NotificationResponseDTO;
import foodmap.V2.websocket.repository.ChatRepository;
import foodmap.V2.websocket.repository.NotificationRepository;

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
    public List<ChatResponseDTO> getChatList() {
        return chatRepository.findAll().stream().map(ChatResponseDTO::new).collect(Collectors.toList());
    }

    public List<NotificationResponseDTO> getNotificationList(Long userId) {
        return notificationRepository.findAllByReciever(userId).stream()
                    .map(NotificationResponseDTO::new)
                    .collect(Collectors.toList());
    }
    public void setNotRead(Long notId) {
        Optional<Notification> notificationOptional = notificationRepository.findById(notId);
        if (notificationOptional.isPresent()) {
            Notification notification = notificationOptional.get();
            notification.setIs_read();
            notificationRepository.save(notification);
        }
    }
}
