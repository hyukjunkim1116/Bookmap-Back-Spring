package foodmap.V2.domain;


import foodmap.V2.domain.post.Post;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

//reporting_user = models.ForeignKey(
//        User, on_delete=models.CASCADE, related_name="reporting_user", null=True
//        )
//reported_user = models.ForeignKey(
//        User, on_delete=models.CASCADE, related_name="reported_user", null=True
//        )



@Getter
@Entity
@Table(
        indexes = {
                @Index(name = "IDX_REPORT_POST_ID", columnList = "post_id")
        }
)
public class Report {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long reportingUser;

    @Column(nullable = false)
    private Long reportedUser;

    @Setter
    @Column(nullable = false)
    private String title;

    @Setter
    @Column(nullable = false)
    private String content;

    private LocalDateTime createdAt;

    @Setter
    @ManyToOne
    @JoinColumn
    private Post post;
    @Builder
    public Report(Long reportingUser, Long reportedUser, String title,String content,Post post) {
        this.reportingUser = reportingUser;
        this.reportedUser = reportedUser;
        this.title = title;
        this.content = content;
        this.post = post;
        this.createdAt = LocalDateTime.now(); // 현재 일시를 생성 일시로 설정

    }
}
