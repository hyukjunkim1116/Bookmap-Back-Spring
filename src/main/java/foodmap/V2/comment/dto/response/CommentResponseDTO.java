package foodmap.V2.comment.dto.response;

import foodmap.V2.comment.domain.Comment;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentResponseDTO {
    private final Long id;
    private final String author;
    private final String comment;

    // 생성자 오버로딩
    public CommentResponseDTO(Comment comment) {
        this.id = comment.getId();
        this.author = String.valueOf(comment.getAuthor());
        this.comment = comment.getComment();
    }

    @Builder
    public CommentResponseDTO(Long id, String author, String comment) {
        this.id = id;
        this.author = author;
        this.comment = comment;
    }
}