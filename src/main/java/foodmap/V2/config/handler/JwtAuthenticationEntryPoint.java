package foodmap.V2.config.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import foodmap.V2.exception.ErrorResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
            log.error("[인증오류] 로그인이 필요합니다.");
            log.info(String.valueOf(authException));
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .code("401")
                    .message("로그인이 필요합니다.")
                    .build();
            response.setContentType(APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(UTF_8.name());
            response.setStatus(SC_UNAUTHORIZED);
            objectMapper.writeValueAsString(errorResponse);

    }
}
