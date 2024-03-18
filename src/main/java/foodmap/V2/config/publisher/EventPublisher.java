package foodmap.V2.config.publisher;


import foodmap.V2.domain.post.Post;

import foodmap.V2.dto.response.EventNotificationDTO;
import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;

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
