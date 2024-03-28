package foodmap.V2.kakao.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KakaoAccount {
    private String email;
    private String name;
    private KakaoProfileInfo profile;
}
