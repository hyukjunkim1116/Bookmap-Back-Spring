package foodmap.V2.dto.response;

import foodmap.V2.domain.Notification;
import lombok.Builder;
import lombok.Getter;

@Getter
public class NotificationResponse {
    private final Long id;
    private final Long receiver;
    private final Boolean is_read;
    private final String message;
    private final String created_at;
    @Builder
    // 생성자 오버로딩
    public NotificationResponse(Notification not) {
        this.id = not.getId();
        this.receiver = not.getReciever();
        this.is_read = not.getIs_read();
        this.message = not.getMessage();
        this.created_at = not.getCreated_at();
    }
}
