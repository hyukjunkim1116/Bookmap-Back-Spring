package foodmap.V2.dto.request.comment;

import lombok.Getter;
import lombok.Setter;

import static java.lang.Math.max;
import static java.lang.Math.min;


@Getter
@Setter

public class CommentSearch {

    private static final int MAX_SIZE = 2000;

    private Integer page;

    private Integer size;
    public CommentSearch() {
        this.page = 1;
        this.size = 6;
    }
    public long getOffset() {
        return (long) (max(1, page) - 1) * min(size, MAX_SIZE);
    }
}