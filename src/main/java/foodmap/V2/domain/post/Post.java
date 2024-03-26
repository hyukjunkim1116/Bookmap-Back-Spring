package foodmap.V2.domain.post;
import foodmap.V2.domain.Comment;
import foodmap.V2.domain.UserInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long readCount;
    private String title;
    private LocalDateTime createdAt=LocalDateTime.now();
    @Lob
    private String content;
    private String image;
    @ManyToOne
    @JoinColumn
    private UserInfo user;
    @ElementCollection(fetch = FetchType.LAZY)
    private List<Long> bookmark=new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    private List<Long> dislike=new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    private List<Long> likedUser=new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    private List<Comment> comments=new ArrayList<>();

    @Builder
    public Post(String title, String content, UserInfo user) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.readCount = 0L;
        this.createdAt = LocalDateTime.now();
    }

    public PostEditor.PostEditorBuilder toEditor() {
        return PostEditor.builder()
                .title(title)
                .content(content);
    }

    public void edit(PostEditor postEditor) {
        title = postEditor.getTitle();
        content = postEditor.getContent();
    }
    public Long getUserId() {
        return this.user.getId();
    }
    // 조회수 증가 메서드 수정
    public void increaseReadCount() {
        this.readCount++; // 조회수를 1 증가시킴
    }
    public void addComment(Comment comment) {
        comment.setPost(this);
        this.comments.add(comment);
    }
}
