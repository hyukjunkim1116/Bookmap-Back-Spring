package foodmap.V2.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDetailResponseDTO {
    private Long author;
    private String comment;
    private String created_at;
    private Long id;
    private String image;
    private Long post;
    private String updated_at;
    private String username;
}