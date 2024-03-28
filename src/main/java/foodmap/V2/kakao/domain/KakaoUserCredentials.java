package foodmap.V2.kakao.domain;
import lombok.*;
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KakaoUserCredentials {
    private String email;
    private String password;
}

