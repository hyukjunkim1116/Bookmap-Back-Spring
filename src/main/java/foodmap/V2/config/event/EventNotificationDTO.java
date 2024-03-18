package foodmap.V2.config.event;

import foodmap.V2.domain.post.Post;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventNotificationDTO {
    private Long receiverId;
    private Long senderId;
    private Post post;
}
