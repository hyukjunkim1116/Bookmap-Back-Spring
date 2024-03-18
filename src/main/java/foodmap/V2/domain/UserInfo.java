package foodmap.V2.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import foodmap.V2.domain.post.Post;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USERS")
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    private String email;
    private Boolean social; // 소셜 로그인 여부
    private Boolean isVerified; // 사용자 인증 여부
    private String image; // 사용자 이미지 URL
    private String username;
    private LocalDateTime createdAt; // 생성 일시
    @Column(nullable = false)
    @JsonIgnore
    private String password;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<UserRole> roles = new HashSet<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Post> post;
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }
    public void setIsVerified() {
        this.isVerified = true;
    }
    public void changeImage(String image) {
        this.image = image;
    }

    public void changeUserInfo(String email,String username) {
        this.email = email;
        this.username=username;
    }

    @Builder
    public UserInfo(String username, String email, String password,Boolean social,String image,Boolean isVerified) {
        this.username = username;
        this.email = email;
        this.image=image;
        this.password = password;
        this.isVerified = isVerified;
        this.social=social;
        this.createdAt = LocalDateTime.now(); // 현재 일시를 생성 일시로 설정
    }
}
