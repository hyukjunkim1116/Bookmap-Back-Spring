package foodmap.V2.comment.dto.request;

import lombok.Getter;
import lombok.Setter;

import static java.lang.Math.max;
import static java.lang.Math.min;


@Getter
@Setter
public class CommentSearchRequestDTO {

    private static final int MAX_SIZE = 2000;

    private Integer page;

    private Integer size;
    public CommentSearchRequestDTO() {
        this.page = 1;
        this.size = 6;
    }
    public long getOffset() {
        return (long) (max(1, page) - 1) * min(size, MAX_SIZE);
    }
}