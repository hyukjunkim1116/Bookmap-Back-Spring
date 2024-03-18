package foodmap.V2.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final AppConfig appConfig;
    public static final String ALLOWED_METHOD_NAMES = "GET,HEAD,POST,PUT,DELETE,TRACE,OPTIONS,PATCH";
    // 이 클래스는 애플리케이션의 Cross-Origin Resource Sharing (CORS)를 설정합니다.
    // CORS는 웹 서버가 자원에 접근할 수 있는 대상을 지정할 수 있게 해주는 중요한 보안 기능입니다.
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 교차 출처 요청에 대한 매핑 추가
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:9030","http://localhost:8080")
                .allowCredentials(true) // 쿠키, 인증 헤더 등의 자격 증명 허용
                .allowedHeaders("Authorization", "Content-Type","Access-Control-Allow-Origin")
                .allowedMethods(ALLOWED_METHOD_NAMES.split(","));
    }
}
