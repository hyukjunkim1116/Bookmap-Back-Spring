package foodmap.V2.websocket.dto.response;

import foodmap.V2.websocket.domain.Chat;

import lombok.Builder;
import lombok.Getter;


@Getter
public class ChatResponseDTO {
    private final Long id;
    private final String sender;
    private final Long sender_id;
    private final String content;
    private final String timestamp;
    private final String sender_image;
    @Builder
    // 생성자 오버로딩
    public ChatResponseDTO(Chat chat) {
        this.id = chat.getId();
        this.sender = chat.getSender();
        this.sender_id = chat.getSender_id();
        this.timestamp = String.valueOf(chat.getTimestamp());
        this.sender_image = chat.getSender_image();
        this.content = chat.getContent();
    }
}
