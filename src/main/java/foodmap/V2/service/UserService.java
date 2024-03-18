package foodmap.V2.service;

import foodmap.V2.config.kakao.KakaoUserCredentials;
import foodmap.V2.config.kakao.KakaoUserInfoResponse;
import foodmap.V2.repository.UserRepository;
import foodmap.V2.domain.UserInfo;
import foodmap.V2.dto.request.ChangePasswordRequestDTO;
import foodmap.V2.dto.request.EditUserInfoDTO;
import foodmap.V2.dto.request.SignUpRequestDTO;
import foodmap.V2.exception.user.AlreadyExistsEmailException;
import foodmap.V2.exception.user.InvalidPassword;
import foodmap.V2.exception.user.UserNotFound;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    public UserInfo getUserByEmail(String email) {
        return userRepository.findByEmail(String.valueOf(email));
    }
    public Optional<UserInfo> getUserById(Long id) {
        return userRepository.findById(id);
    }
    public Optional<UserInfo> getIfUserByEmail(String email) {
        return userRepository.findUserInfoByEmail(String.valueOf(email));
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
    public KakaoUserCredentials kakaoSignUp(KakaoUserInfoResponse kakaoUserInfoResponse) {
        Optional<UserInfo> userOptional = Optional.ofNullable(userRepository.findByEmail(kakaoUserInfoResponse.getKakao_account().getEmail()));
        String orgPassword = kakaoUserInfoResponse.getKakao_account().getEmail();
        String encryptedPassword = passwordEncoder.encode(orgPassword);
        log.info("kakao,{}", kakaoUserInfoResponse.getKakao_account().getProfile());
        if (userOptional.isPresent()) {
            var user = userOptional.get();
            Boolean isSocial = user.getSocial();
            if (isSocial == null) {
                throw new AlreadyExistsEmailException();
            } else {
                return KakaoUserCredentials.builder()
                        .email(kakaoUserInfoResponse.getKakao_account().getEmail())
                        .password(orgPassword).build();
            }
        }
        var user = UserInfo.builder()
                .email(kakaoUserInfoResponse.getKakao_account().getEmail())
                .password(encryptedPassword)
                .image(kakaoUserInfoResponse.getKakao_account().getProfile().getThumbnail_image_url())
                .social(true)
                .isVerified(false)
                .username(kakaoUserInfoResponse.getKakao_account().getProfile().getNickname())
                .build();
        userRepository.save(user);
        return KakaoUserCredentials.builder()
                .email(kakaoUserInfoResponse.getKakao_account().getEmail())
                .password(orgPassword).build();
    }
    //존재하는 user일 경우 임시 비번 생성
    public String getTempPassword(){
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

    //임시로 생성한 비번 encoding
    public void changePassword(UserInfo user, String password){
        String encryptedPassword = passwordEncoder.encode(password);
        user.changePassword(encryptedPassword);
        userRepository.save(user);
    }
    public void verifyUser(UserInfo user){
        user.setIsVerified();
        userRepository.save(user);
    }
    public void authChangePassword(UserInfo user, ChangePasswordRequestDTO changePasswordRequestDTO) {
        if (!user.getPassword().equals(passwordEncoder.encode(changePasswordRequestDTO.getOldPassword())) && !changePasswordRequestDTO.getNewPassword().equals(changePasswordRequestDTO.getNewPasswordConfirm())) {
            throw new InvalidPassword();
        }
        String encryptedPassword = passwordEncoder.encode(changePasswordRequestDTO.getNewPassword());
        user.changePassword(encryptedPassword);
        userRepository.save(user);
    }
    public void changeUserInfo(Long uid, EditUserInfoDTO editUserInfoDTO){
        UserInfo user = this.getUserById(uid).orElseThrow(UserNotFound::new);
        user.changeUserInfo(editUserInfoDTO.getEmail(),editUserInfoDTO.getUsername());
        userRepository.save(user);
    }
    public void deleteUser(UserInfo user) {
        String image = user.getImage();
        if (image != null) {
            s3Service.deleteImage(image);
        }
        userRepository.delete(user);
    }
    public String saveUserImage(Long uid, MultipartFile image) throws IOException {
        UserInfo user = this.getUserById(uid).orElseThrow(UserNotFound::new);
        if (user.getImage() != null) {
            log.info("uimage,{}",user.getImage());
            s3Service.deleteImage(user.getImage());
        }
        String imageUrl=s3Service.saveFile(image);
        user.changeImage(imageUrl);
        userRepository.save(user);
        return imageUrl;
    }

}
