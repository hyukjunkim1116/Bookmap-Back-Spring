package foodmap.V2.kakao.dto.response;

import foodmap.V2.kakao.domain.KakaoAccount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoUserInfoResponseDTO {
    private KakaoAccount kakao_account;
}
