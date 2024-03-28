package foodmap.V2.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class CommentCreateRequestDTO {
    @Length(min = 10, max = 1000, message = "내용은 10~1000자까지 입력해주세요.")
    @NotBlank(message = "내용을 입력해주세요.")
    private final String comment;
    private final String author;

    @Builder
    public CommentCreateRequestDTO(String author, String content) {
       this.author=author;
        this.comment = content;
    }

}
