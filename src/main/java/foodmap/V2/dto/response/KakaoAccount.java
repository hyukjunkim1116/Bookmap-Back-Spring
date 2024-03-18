package foodmap.V2.dto.response;

import lombok.Data;
@Data
public class KakaoAccount {
    private String email;
    private String name;
    private KakaoProfileInfo profile;
}
