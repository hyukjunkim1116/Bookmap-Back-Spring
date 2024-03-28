package foodmap.V2.websocket.dto.response;

import foodmap.V2.post.domain.Post;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventNotificationResponseDTO {
    private Long receiverId;
    private Long senderId;
    private Post post;
}
