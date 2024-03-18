package foodmap.V2.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KakaoUserCredentials {
    private String email;
    private String password;
}

