package foodmap.V2.config.kakao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

// 인가코드를 이용하여 Token ( Access , Refresh )를 받는다.
@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoTokenJsonData {
    private final WebClient webClient;
    private static final String TOKEN_URI = "https://kauth.kakao.com/oauth/token";
    private static final String REDIRECT_URI = "http://localhost:9030/social/kakao";
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
        log.info("token,{}",response);
        return response.blockFirst();
    }
}
