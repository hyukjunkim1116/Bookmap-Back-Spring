package foodmap.V2.config.kakao;

import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


// 인가코드를 이용하여 Token ( Access , Refresh )를 받는다.
@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {
    private final RestTemplate restTemplate;
    private final WebClient webClient;
    private static final String MESSAGE_URI = "https://kapi.kakao.com/v2/api/talk/memo/default/send";

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String CLIENT_ID;

//    public void sendMessage(String token) {
//        String orgToken = token.replaceAll("^\"|\"$", "");
//        String tokenValue = String.format("Bearer %s",orgToken);
//        String requestBody = "{\"template_object\":{\"text\":\"" + "로그인 완료" + "\"}}";
//        webClient.post()
//                .uri(MESSAGE_URI)
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                .header("Authorization", tokenValue)
//                .body(requestBody);
//    }
public void sendKakaoMessage(String accessToken) {

    HttpHeaders CustomHeaders = new HttpHeaders();
    CustomHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    CustomHeaders.setBearerAuth(accessToken);
    JSONObject requestBody = getJsonObject();

    log.info(requestBody.toString());

    HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), CustomHeaders);
    ResponseEntity<String> response = restTemplate.exchange(
            MESSAGE_URI,
            HttpMethod.POST,
            entity,
            String.class
    );
    if (response.getStatusCode() == HttpStatus.OK) {
        System.out.println("카카오톡 메시지가 성공적으로 전송되었습니다.");
    } else {
        System.out.println("카카오톡 메시지 전송에 실패하였습니다. 응답 코드: " + response.getStatusCode());
    }
}

    private static JSONObject getJsonObject() {
        JSONObject requestBody = new JSONObject();
        JSONObject templateObject = new JSONObject();
        templateObject.put("text", "로그인 완료");
        templateObject.put("button_title", "버튼 제목");

        JSONObject button = new JSONObject();
        button.put("title", "버튼1");

        JSONObject link = new JSONObject();
        link.put("web_url", "https://example.com");
        link.put("mobile_web_url", "https://example.com");
        button.put("link", link);
        JSONArray buttons = new JSONArray();
        buttons.put(button);

        templateObject.put("buttons", buttons);
        requestBody.put("template_object", templateObject);
        return requestBody;
    }
}
