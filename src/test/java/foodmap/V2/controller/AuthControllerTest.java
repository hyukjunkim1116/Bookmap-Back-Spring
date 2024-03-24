package foodmap.V2.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import foodmap.V2.config.FoodmapMockUser;
import foodmap.V2.domain.UserInfo;
import foodmap.V2.dto.request.*;
import foodmap.V2.repository.UserRepository;
import foodmap.V2.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.Map;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @BeforeEach
    void before() {
        UserInfo user = UserInfo.builder()
                .email("1@1.com")
                .password(passwordEncoder.encode("1234"))
                .username("test")
                .image(null)
                .isVerified(false)
                .social(false)
                .build();
        userRepository.save(user);
    }
    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }
    private Map<String, Object> LoginAndGetResponse() throws Exception {
        LoginRequestDTO loginRequestDTO = LoginRequestDTO.builder()
                .email("1@1.com")
                .password("1234")
                .build();
        MvcResult result= mockMvc.perform(post("/api/users/login/")
                        .content(objectMapper.writeValueAsString(loginRequestDTO))
                        .contentType(APPLICATION_JSON))
                        .andReturn();
        MockHttpServletResponse response = result.getResponse();
        return objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
    }

    @Test
    @DisplayName("회원가입")
    void signup() throws Exception {
        // given
        SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder()
                .email("2@2.com")
                .password("1234")
                .passwordConfirm("1234")
                .username("signup")
                .build();
        //when
        // expected
        mockMvc.perform(post("/api/users/")
                        .content(objectMapper.writeValueAsString(signUpRequestDTO))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 후 토큰 발급")
    void authenticateAndGetToken() throws Exception {
        // given
        LoginRequestDTO loginRequestDTO = LoginRequestDTO.builder()
                .email("1@1.com")
                .password("1234")
                .build();
        // when
        // expected
        mockMvc.perform(post("/api/users/login/")
                        .content(objectMapper.writeValueAsString(loginRequestDTO))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/users/login/")
                        .content(objectMapper.writeValueAsString(loginRequestDTO))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void kakaoLogin() {
    }

    @Test
    void changePassword() throws Exception {
        // given
        Map<String, Object> response = this.LoginAndGetResponse();
        String accessToken = (String) response.get("access");
        log.info("access1234 : {}",response);
        ChangePasswordRequestDTO changePasswordRequestDTO = ChangePasswordRequestDTO.builder()
                .oldPassword("1234")
                .newPassword("2345")
                .newPasswordConfirm("2345")
                .build();
        mockMvc.perform(put("/api/users/change-password/")
                        .header("Authorization","Bearer "+accessToken)
                        .content(objectMapper.writeValueAsString(changePasswordRequestDTO))
                        .contentType(APPLICATION_JSON))
                        .andExpect(status().isOk());
        LoginRequestDTO loginRequestDTO1 = LoginRequestDTO.builder()
                .email("1@1.com")
                .password("2345")
                .build();
        mockMvc.perform(post("/api/users/login/")
                        .content(objectMapper.writeValueAsString(loginRequestDTO1))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void sendVerifyEmail() {
    }

    @Test
    void verifyEmailPermit() {
    }

    @Test
    void refreshToken() throws Exception {
        //given
        Map<String, Object> response = this.LoginAndGetResponse();
        String refreshToken = (String) response.get("refresh");
        RefreshTokenRequestDTO refreshTokenRequestDTO = RefreshTokenRequestDTO.builder()
                .refresh(refreshToken).build();
        //expected
        mockMvc.perform(post("/api/users/token/refresh/")
                        .content(objectMapper.writeValueAsString(refreshTokenRequestDTO))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    void findPassword() throws Exception {
        EmailRequestDTO emailRequestDTO = EmailRequestDTO.builder()
                .email("1@1.com")
                .build();
        //expected
        mockMvc.perform(put("/api/users/find-password/")
                        .content(objectMapper.writeValueAsString(emailRequestDTO))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void editUser() throws Exception {
        //given
        Map<String, Object> response = this.LoginAndGetResponse();
        String accessToken = (String) response.get("access");
        EditUserInfoDTO editUserInfoDTO = EditUserInfoDTO.builder()
                .email("11@11.com")
                .username("kimtest").build();
        String auth = String.format("Bearer %s",accessToken);
        //expected
        mockMvc.perform(put("/api/users/1/")
                        .header("Authorization",auth)
                        .content(objectMapper.writeValueAsString(editUserInfoDTO))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser() throws Exception {
        //given
        Map<String, Object> response = this.LoginAndGetResponse();
        String accessToken = (String) response.get("access");
        String auth = String.format("Bearer %s",accessToken);
        //expected
        mockMvc.perform(delete("/api/users/1/")
                        .header("Authorization",auth)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void editUserImage() throws Exception {
        //given
        Map<String, Object> response = this.LoginAndGetResponse();
        String accessToken = (String) response.get("access");
        String auth = String.format("Bearer %s",accessToken);
        MockMultipartFile file = new MockMultipartFile(
                "image",      // 파라미터 이름
                "filename.jpg", // 파일 이름
                MediaType.IMAGE_JPEG_VALUE, // 파일 타입
                "test data".getBytes());    // 파일 데이터
        mockMvc.perform(
                        multipart("/api/users/1/image/")
                                .file(file)  // 파일 전달
                                .header("Authorization", auth))
                .andExpect(status().isOk());
    }
}