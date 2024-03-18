package foodmap.V2.config.kakao;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KakaoUserCredentials {
    private String email;
    private String password;
}

