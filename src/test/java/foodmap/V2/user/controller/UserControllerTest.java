package foodmap.V2.user.controller;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import foodmap.V2.config.BookmapMockUser;
import foodmap.V2.config.S3MockConfig;
import foodmap.V2.jwt.RefreshTokenRequestDTO;
import foodmap.V2.user.domain.UserInfo;
import foodmap.V2.user.dto.request.ChangePasswordRequestDTO;
import foodmap.V2.user.dto.request.EditUserInfoRequestDTO;
import foodmap.V2.user.dto.request.LoginRequestDTO;
import foodmap.V2.user.dto.request.SignUpRequestDTO;
import foodmap.V2.user.repository.UserRepository;
import foodmap.V2.util.email.EmailRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.Map;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@Slf4j
@SpringBootTest
@Import(S3MockConfig.class)
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }
    private Map<String, Object> LoginAndGetResponse() throws Exception {
        UserInfo user = UserInfo.builder()
                .email("1@1.com")
                .password(passwordEncoder.encode("1234"))
                .username("test")
                .image(null)
                .isVerified(false)
                .social(false)
                .build();
        userRepository.save(user);
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
    void normalLogin() throws Exception {
        // given
        UserInfo user = UserInfo.builder()
                .email("1@1.com")
                .password(passwordEncoder.encode("1234"))
                .username("test")
                .image(null)
                .isVerified(false)
                .social(false)
                .build();
        userRepository.save(user);
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
    @BookmapMockUser
    @DisplayName("로그인 유저의 비밀번호 변경")
    void changePassword() throws Exception {
        // given
        ChangePasswordRequestDTO changePasswordRequestDTO = ChangePasswordRequestDTO.builder()
                .oldPassword("1")
                .newPassword("2345")
                .newPasswordConfirm("2345")
                .build();
        //expected
        mockMvc.perform(put("/api/users/change-password/")
                        .content(objectMapper.writeValueAsString(changePasswordRequestDTO))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("로그인 한 유저의 엑세스 토큰 재발급")
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
    @DisplayName("로그인 안한 유저의 비밀번호 찾기")
    void findPassword() throws Exception {
        //given
        UserInfo user = UserInfo.builder()
                .email("1@1.com")
                .password(passwordEncoder.encode("1234"))
                .username("test")
                .image(null)
                .isVerified(false)
                .social(false)
                .build();
        userRepository.save(user);
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
    @DisplayName("로그인 한 유저의 정보 변경")
    void editUser() throws Exception {
        //given
        Map<String, Object> response = this.LoginAndGetResponse();
        String access = (String) response.get("access");
        String email = (String) response.get("email");
        UserInfo user = userRepository.findByEmail(email);
        EditUserInfoRequestDTO editUserInfoRequestDTO = EditUserInfoRequestDTO.builder()
                .email("11@11.com")
                .username("kimtest").build();
        //expected
        mockMvc.perform(put("/api/users/"+user.getId() +"/")
                        .header("Authorization","Bearer "+access)
                        .content(objectMapper.writeValueAsString(editUserInfoRequestDTO))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그인 한 유저의 계정 삭제")
    void deleteUser() throws Exception {
        //given
        Map<String, Object> response = this.LoginAndGetResponse();
        String access = (String) response.get("access");
        String email = (String) response.get("email");
        UserInfo user = userRepository.findByEmail(email);
        //expected
        mockMvc.perform(delete("/api/users/"+user.getId() +"/")
                        .header("Authorization","Bearer "+access)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그인 한 유저의 프로필 사진 변경")
    void editUserImage() throws Exception {
        //given
        Map<String, Object> response = this.LoginAndGetResponse();
        String access = (String) response.get("access");
        String email = (String) response.get("email");
        UserInfo user = userRepository.findByEmail(email);
        MockMultipartFile file = new MockMultipartFile(
                "image",      // 파라미터 이름
                "filename.jpg", // 파일 이름
                MediaType.IMAGE_JPEG_VALUE, // 파일 타입
                "test data".getBytes());    // 파일 데이터
        //expected
        mockMvc.perform(
                        multipart("/api/users/"+user.getId() +"/image/")
                                .file(file)
                                .header("Authorization","Bearer "+access))// 파일 전달
                .andExpect(status().isOk())
                .andReturn();
    }
}