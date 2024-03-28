package foodmap.V2.user.service;
import foodmap.V2.config.BookmapMockUser;
import foodmap.V2.config.S3MockConfig;
import foodmap.V2.user.dto.response.UserNewPasswordResponseDTO;
import foodmap.V2.util.AmazonS3.S3Service;

import foodmap.V2.user.domain.UserInfo;
import foodmap.V2.user.dto.request.ChangePasswordRequestDTO;
import foodmap.V2.user.dto.request.EditUserInfoRequestDTO;
import foodmap.V2.user.dto.request.SignUpRequestDTO;
import foodmap.V2.exception.user.InvalidPassword;
import foodmap.V2.user.repository.UserRepository;
import io.findify.s3mock.S3Mock;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
@Slf4j
@Import(S3MockConfig.class)
@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private S3Service s3Service;
    @Autowired
    private S3Mock s3Mock;

    @BeforeEach
    void setUp() {
        SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder()
                .email("1@1.com")
                .password("1234")
                .passwordConfirm("1234")
                .username("kim")
                .build();
        // when
        userService.signup(signUpRequestDTO);
    }
    @AfterEach
    void clean() {
        userRepository.deleteAll();
        s3Mock.stop();
    }

    @Test
    @DisplayName("일반 회원가입 성공")
    void signupSuccess() {
        // given
        SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder()
                .email("2@2.com")
                .password("1234")
                .passwordConfirm("1234")
                .username("김혁준")
                .build();
        // when
        userService.signup(signUpRequestDTO);
        // then
        assertEquals(2, userRepository.count());

        UserInfo userInfo = userRepository.findAll().getLast();
        assertEquals("2@2.com", userInfo.getEmail());
        assertNotNull(userInfo.getPassword());
        assertNotEquals("1234", userInfo.getPassword());
        assertEquals("김혁준", userInfo.getUsername());
    }
    @Test
    @DisplayName("일반 회원가입시 비밀번호 확인 달라서 실패")
    void signupFail() {
        // given
        SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder()
                .email("2@2.com")
                .password("1234")
                .passwordConfirm("4567")
                .username("김혁준")
                .build();
        // when
        // then
        assertThrows(InvalidPassword.class, () -> userService.signup(signUpRequestDTO));
    }
    @Test
    @DisplayName("로그인 후 유저 비밀번호 변경 성공")
    void authChangePassword() {
        //given
        UserInfo user = userRepository.findByEmail("1@1.com");
        ChangePasswordRequestDTO changePasswordRequestDTO = ChangePasswordRequestDTO.builder()
                .oldPassword("1234")
                .newPassword("2345")
                .newPasswordConfirm("2345")
                .build();
        String beforePassword = user.getPassword();
        //when
        userService.authChangePassword(user,changePasswordRequestDTO);
        //then
        assertNotEquals(beforePassword, user.getPassword());
    }
    @Test
    @DisplayName("로그인 없이 비밀번호 변경 성공")
    void findPassword() {
        //given
        UserInfo user = userRepository.findByEmail("1@1.com");
        //when
        UserNewPasswordResponseDTO userNewPasswordResponseDTO = userService.findPassword(user);
        //then
        assertEquals(userNewPasswordResponseDTO.getPassword(), user.getPassword());
    }

    @Test
    @DisplayName("유저 정보 변경 성공")
    void changeUserInfo() {
        //given
        UserInfo user = userRepository.findByEmail("1@1.com");
        EditUserInfoRequestDTO editUserInfoRequestDTO = EditUserInfoRequestDTO.builder()
                .email("3@3.com")
                .username("after")
                .build();
        //when
        userService.changeUserInfo(user,editUserInfoRequestDTO);
        //then
        assertEquals(user.getEmail(), "3@3.com");
        assertEquals(user.getUsername(), "after");
    }

    @Test
    @DisplayName("유저 삭제 성공")
    void deleteUser() {
        //given
        UserInfo user = userRepository.findByEmail("1@1.com");
        //when
        userService.deleteUser(user);
        //then
        assertNull(userRepository.findByEmail("1@1.com"));
    }

    @Test
    @DisplayName("유저 이미지 저장 성공")
    void saveUserImage() throws IOException {
        //given
        String path = "test.png";
        String contentType = "image/png";
        MockMultipartFile file = new MockMultipartFile("test", path, contentType, "test".getBytes());
        UserInfo user = userRepository.findByEmail("1@1.com");
        //when
        var result = userService.saveUserImage(user,file);
        //then
        assertEquals(user.getImage(),result.getImage());
    }
}