package foodmap.V2.event.publisher;

import foodmap.V2.post.domain.Post;
import foodmap.V2.websocket.dto.response.EventNotificationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventPublisher {
    private final ApplicationEventPublisher eventPublisher;
    public void saveNotification(Long receiverId, Long senderId, Post post) {
        EventNotificationResponseDTO eventDTO = EventNotificationResponseDTO.builder()
                .receiverId(receiverId)
                .senderId(senderId)
                .post(post)
                .build();
        eventPublisher.publishEvent(eventDTO);
    }
}
