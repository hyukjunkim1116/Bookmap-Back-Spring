package foodmap.V2.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentEditor {

    private final String comment;

    @Builder
    public CommentEditor(String comment) {
        this.comment = comment;
    }
}
