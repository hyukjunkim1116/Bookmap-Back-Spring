package foodmap.V2.comment.domain;
import foodmap.V2.post.domain.Post;

import foodmap.V2.user.domain.UserInfo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Entity
@Table(
        indexes = {
                @Index(name = "IDX_COMMENT_POST_ID", columnList = "post_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private UserInfo author;

    @Setter
    @Column(nullable = false)
    private String comment;
    private LocalDateTime createdAt;
    @Setter
    @ManyToOne
    @JoinColumn
    private Post post;

    @Builder
    public Comment(UserInfo author, String comment) {
        this.author = author;
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
    }
    public CommentEditor.CommentEditorBuilder toEditor() {
        return CommentEditor.builder()
                .comment(comment);
    }

    public void edit(CommentEditor commentEditor) {
        comment = commentEditor.getComment();
    }
}
