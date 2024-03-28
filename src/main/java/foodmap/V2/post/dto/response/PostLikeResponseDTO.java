package foodmap.V2.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostLikeResponseDTO {
    private List<Long> like;
    private Long likes_count;
    private Boolean is_liked;
}