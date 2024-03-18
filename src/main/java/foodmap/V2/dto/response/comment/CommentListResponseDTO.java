package foodmap.V2.dto.response.comment;
import foodmap.V2.dto.response.post.PostDetailResponseDTO;
import lombok.*;

import java.util.List;
/**
 * 서비스 정책에 맞는 클래스
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentListResponseDTO {
    private Long count;
    private Boolean next;
    private List<CommentDetailResponseDTO> results;
}
