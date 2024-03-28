package foodmap.V2.post.dto.response;

import foodmap.V2.post.dto.response.PostAuthorResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDetailResponseDTO {
    private PostAuthorResponseDTO author;
    private List<Long> bookmark;
    private Long comments_count;
    private String content;
    private String created_at;
    private List<Long> dislike;
    private Long dislikes_count;
    private Long id;
    private String image;
    private Boolean is_bookmarked;
    private Boolean is_disliked;
    private Boolean is_liked;
    private List<Long> like;
    private Long likes_count;
    private Long read_count;
    private String title;
    private String updated_at;
}