package foodmap.V2.config.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import foodmap.V2.domain.post.Post;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    public void saveNotification(Long recieverId, Long senderId, Post post) {
        EventNotificationDTO eventDTO = EventNotificationDTO.builder()
                .receiverId(recieverId)
                .senderId(senderId)
                .post(post)
                .build();
        eventPublisher.publishEvent(eventDTO);
    }
}
