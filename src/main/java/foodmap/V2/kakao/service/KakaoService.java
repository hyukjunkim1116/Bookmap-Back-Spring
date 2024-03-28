package foodmap.V2.kakao.service;
import foodmap.V2.kakao.dto.response.KakaoTokenResponse;
import foodmap.V2.kakao.dto.response.KakaoUserInfoResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {
    private final WebClient webClient;
    private static final String TOKEN_URI = "https://kauth.kakao.com/oauth/token";
    private static final String REDIRECT_URI = "http://localhost:9030/social/kakao";
    private static final String USER_INFO_URI = "https://kapi.kakao.com/v2/user/me";
    private static final String GRANT_TYPE = "authorization_code";
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String CLIENT_ID;
    private static  final String SCOPE= "talk_message";
    public KakaoTokenResponse getData(String code) {
        String orgCode = code.replaceAll("^\"|\"$", "");
        String uri = TOKEN_URI + "?grant_type=" + GRANT_TYPE + "&client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI + "&scope="+SCOPE+"&code=" + orgCode;
        System.out.println(uri);
        Flux<KakaoTokenResponse> response = webClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .retrieve()
                .bodyToFlux(KakaoTokenResponse.class);
        return response.blockFirst();
    }
    public KakaoUserInfoResponseDTO getUserInfo(String token) {
        Flux<KakaoUserInfoResponseDTO> response = webClient.get()
                .uri(USER_INFO_URI)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToFlux(KakaoUserInfoResponseDTO.class);
        return response.blockFirst();
    }
}
