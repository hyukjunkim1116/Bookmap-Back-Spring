package foodmap.V2.user.service;

import foodmap.V2.exception.user.InvalidRequest;
import foodmap.V2.jwt.JwtService;
import foodmap.V2.jwt.RefreshToken;
import foodmap.V2.user.dto.request.LoginRequestDTO;
import foodmap.V2.user.dto.response.UserCreateResponseDTO;
import foodmap.V2.user.dto.response.UserNewPasswordResponseDTO;
import foodmap.V2.util.ImageResponseDTO;
import foodmap.V2.kakao.domain.KakaoUserCredentials;
import foodmap.V2.kakao.dto.response.KakaoUserInfoResponseDTO;
import foodmap.V2.user.repository.UserRepository;
import foodmap.V2.user.domain.UserInfo;
import foodmap.V2.user.dto.request.ChangePasswordRequestDTO;
import foodmap.V2.user.dto.request.EditUserInfoRequestDTO;
import foodmap.V2.user.dto.request.SignUpRequestDTO;
import foodmap.V2.exception.user.AlreadyExistsEmailException;
import foodmap.V2.exception.user.InvalidPassword;
import foodmap.V2.exception.user.UserNotFound;
import foodmap.V2.util.AmazonS3.S3Service;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final JwtService jwtService;
    public UserInfo getUserByEmail(String email) {
        return userRepository.findByEmail(String.valueOf(email));
    }
    public Optional<UserInfo> getUserById(Long id) {
        return userRepository.findById(id);
    }
    public Optional<UserInfo> getIfUserByEmail(String email) {
        return userRepository.findUserInfoByEmail(String.valueOf(email));
    }
    public UserInfo findUserByRefresh(String refresh){
        return userRepository.findByRefreshTokenRefresh(refresh);
    }
    public void verifyUser(UserInfo user){
        user.setIsVerified();
        userRepository.save(user);
    }
    public void signup(SignUpRequestDTO signUpRequestDTO) {
        Optional<UserInfo> userOptional = Optional.ofNullable(userRepository.findByEmail(signUpRequestDTO.getEmail()));
        if (userOptional.isPresent()) {
            throw new AlreadyExistsEmailException();
        }
        if (!signUpRequestDTO.getPassword().equals(signUpRequestDTO.getPasswordConfirm())) {
            throw new InvalidPassword();
        }
        String encryptedPassword = passwordEncoder.encode(signUpRequestDTO.getPassword());
        var user = UserInfo.builder()
                .email(signUpRequestDTO.getEmail())
                .password(encryptedPassword)
                .username(signUpRequestDTO.getUsername())
                .image(null)
                .isVerified(false)
                .social(false)
                .build();
        userRepository.save(user);
    }
    @Transactional
    public KakaoUserCredentials kakaoSignUp(KakaoUserInfoResponseDTO kakaoUserInfoResponseDTO) {
        Optional<UserInfo> userOptional = Optional.ofNullable(userRepository.findByEmail(kakaoUserInfoResponseDTO.getKakao_account().getEmail()));
        String orgPassword = kakaoUserInfoResponseDTO.getKakao_account().getEmail();
        String encryptedPassword = passwordEncoder.encode(orgPassword);
        log.info("kakao,{}", kakaoUserInfoResponseDTO.getKakao_account().getProfile());
        if (userOptional.isPresent()) {
            UserInfo user = userOptional.get();
            Boolean isSocial = user.getSocial();
            if (isSocial == null) {
                throw new AlreadyExistsEmailException();
            } else {
                return KakaoUserCredentials.builder()
                        .email(kakaoUserInfoResponseDTO.getKakao_account().getEmail())
                        .password(orgPassword).build();
            }
        }
        UserInfo user = UserInfo.builder()
                .email(kakaoUserInfoResponseDTO.getKakao_account().getEmail())
                .password(encryptedPassword)
                .image(kakaoUserInfoResponseDTO.getKakao_account().getProfile().getThumbnail_image_url())
                .social(true)
                .isVerified(false)
                .username(kakaoUserInfoResponseDTO.getKakao_account().getProfile().getNickname())
                .build();
        userRepository.save(user);
        return KakaoUserCredentials.builder()
                .email(kakaoUserInfoResponseDTO.getKakao_account().getEmail())
                .password(orgPassword).build();
    }
    public void authChangePassword(UserInfo user, ChangePasswordRequestDTO changePasswordRequestDTO) {
        if (!user.getPassword().equals(passwordEncoder.encode(changePasswordRequestDTO.getOldPassword())) && !changePasswordRequestDTO.getNewPassword().equals(changePasswordRequestDTO.getNewPasswordConfirm())) {
            throw new InvalidPassword();
        }
        String encryptedPassword = passwordEncoder.encode(changePasswordRequestDTO.getNewPassword());
        user.changePassword(encryptedPassword);
        userRepository.save(user);
    }
    public UserNewPasswordResponseDTO findPassword(UserInfo user) {
        String newPassword = this.getTempPassword();
        user.changePassword(newPassword);
        return UserNewPasswordResponseDTO.builder()
                .password(newPassword).build();
    }
    public void changeUserInfo(UserInfo user, EditUserInfoRequestDTO editUserInfoRequestDTO){
        user.changeUserInfo(editUserInfoRequestDTO.getEmail(), editUserInfoRequestDTO.getUsername());
        userRepository.save(user);
    }
    public void changeUserRefreshToken(UserInfo user,RefreshToken refreshToken){
        user.changeUserRefreshToken(refreshToken);
        userRepository.save(user);
    }
    public void deleteUser(UserInfo user) {
        String image = user.getImage();
        if (image != null) {
            s3Service.deleteImage(image);
        }
        userRepository.delete(user);
    }
    public ImageResponseDTO saveUserImage(UserInfo user, MultipartFile image) throws IOException {
        if (user.getImage() != null) {
            s3Service.deleteImage(user.getImage());
        }
        String imageUrl=s3Service.saveFile(image);
        user.changeImage(imageUrl);
        userRepository.save(user);
        return ImageResponseDTO.builder()
                .image(imageUrl)
                .build();
    }
    public UserCreateResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Authentication authentication = this.authenticate(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());
        UserInfo user = this.getUserIfAuthenticated(authentication, loginRequestDTO.getEmail());
        RefreshToken refreshToken = jwtService.createRefreshToken();
        this.changeUserRefreshToken(user, refreshToken);
        return this.buildUserCreateResponseDTO(user,refreshToken);
    }
    public Optional<UserInfo> getUserByRequest(HttpServletRequest request) {
        String requestUserEmail = request.getRemoteUser();
        if (requestUserEmail == null) {
            return Optional.empty();
        }
        return userRepository.findUserInfoByEmail(requestUserEmail);
    }
    private Authentication authenticate(String email, String password) {
        return authenticationManagerBuilder.getObject().authenticate(new UsernamePasswordAuthenticationToken(email, password));
    }
    private UserInfo getUserIfAuthenticated(Authentication authentication, String email) {
        if (authentication.isAuthenticated()) {
            return this.getUserByEmail(email);
        } else {
            throw new InvalidRequest();
        }
    }
    private UserCreateResponseDTO buildUserCreateResponseDTO(UserInfo user,RefreshToken refreshToken) {
        return UserCreateResponseDTO.builder()
                .is_verified(user.getIsVerified())
                .social(user.getSocial())
                .username(user.getUsername())
                .image(user.getImage())
                .email(user.getEmail())
                .access(jwtService.generateToken(user.getEmail(), String.valueOf(user.getId())))
                .refresh(refreshToken.getRefresh())
                .build();
    }
    private String getTempPassword(){
        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

        StringBuilder tempPwd = new StringBuilder();

        int idx;
        for (int i = 0; i < 10; i++) {
            idx = (int) (charSet.length * Math.random());
            tempPwd.append(charSet[idx]);
        }
        return tempPwd.toString();
    }
}
