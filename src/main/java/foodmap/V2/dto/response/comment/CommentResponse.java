package foodmap.V2.dto.response.comment;

import foodmap.V2.domain.Comment;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentResponse {
    private final Long id;
    private final String author;
    private final String comment;

    // 생성자 오버로딩
    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.author = String.valueOf(comment.getAuthor());
        this.comment = comment.getComment();
    }

    @Builder
    public CommentResponse(Long id, String author, String comment) {
        this.id = id;
        this.author = author;
        this.comment = comment;
    }
}