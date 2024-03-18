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
public class PostBookmarkDTO {
    private List<Long> bookmark;
    private Boolean is_bookmarked;
}