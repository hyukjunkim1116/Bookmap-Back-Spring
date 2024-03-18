package foodmap.V2.dto.response.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostDislikeDTO {
    private List<Long> dislike;
    private Long dislikes_count;
    private Boolean is_disliked;
}