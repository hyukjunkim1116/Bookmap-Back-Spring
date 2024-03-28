package foodmap.V2.user.controller;


import foodmap.V2.exception.email.EmailSendFail;
import foodmap.V2.kakao.domain.KakaoUserCredentials;
import foodmap.V2.user.dto.response.UserNewPasswordResponseDTO;
import foodmap.V2.util.ImageResponseDTO;
import foodmap.V2.kakao.dto.response.KakaoTokenResponse;
import foodmap.V2.kakao.dto.response.KakaoUserInfoResponseDTO;
import foodmap.V2.user.dto.response.UserCreateResponseDTO;
import foodmap.V2.util.email.EmailRequestDTO;
import foodmap.V2.user.dto.request.ChangePasswordRequestDTO;
import foodmap.V2.jwt.RefreshToken;
import foodmap.V2.user.domain.UserInfo;
import foodmap.V2.jwt.JwtResponseDTO;
import foodmap.V2.exception.user.AccessDenied;
import foodmap.V2.exception.jwt.RefreshTokenExpired;
import foodmap.V2.exception.user.UserNotFound;
import foodmap.V2.util.email.EmailService;
import foodmap.V2.jwt.JwtService;
import foodmap.V2.user.service.UserService;
import foodmap.V2.user.dto.request.EditUserInfoRequestDTO;
import foodmap.V2.user.dto.request.LoginRequestDTO;
import foodmap.V2.jwt.RefreshTokenRequestDTO;
import foodmap.V2.user.dto.request.SignUpRequestDTO;
import foodmap.V2.kakao.service.KakaoService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Objects;
import java.util.Optional;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final KakaoService kakaoService;
    @PostMapping("/")
    public void signup(@RequestBody SignUpRequestDTO signUpRequestDTO){
        userService.signup(signUpRequestDTO);
    }
    @PostMapping("/login/")
    public UserCreateResponseDTO normalLogin(@RequestBody LoginRequestDTO loginRequestDTO) {
        return  userService.login(loginRequestDTO);
    }
    @PostMapping("/kakao/")
    public UserCreateResponseDTO kakaoLogin(@RequestBody String code) {
        KakaoTokenResponse kakaoTokenResponse = kakaoService.getData(code);
        KakaoUserInfoResponseDTO userInfo = kakaoService.getUserInfo(kakaoTokenResponse.getAccess_token());
        KakaoUserCredentials savedUser = userService.kakaoSignUp(userInfo);
        LoginRequestDTO loginRequestDTO= LoginRequestDTO.builder()
                .password(savedUser.getPassword())
                .email(savedUser.getEmail()).build();
        return userService.login(loginRequestDTO);
    }
    @PutMapping("/change-password/")
    public void changePassword(HttpServletRequest request, @RequestBody ChangePasswordRequestDTO changePasswordRequestDTO){
        UserInfo user =userService.getUserByRequest(request).orElseThrow(UserNotFound::new);
        userService.authChangePassword(user,changePasswordRequestDTO);
    }
    @PostMapping("/{uid}/verify/")
    public void sendVerifyEmail(HttpServletRequest request,@PathVariable Long uid) {
        Optional<UserInfo> userInfoOptional =userService.getUserByRequest(request);
        userInfoOptional.ifPresentOrElse(
                 userInfo -> {
                     try {
                         emailService.sendHtmlEmail(userInfo.getEmail(),uid);
                     } catch (MessagingException e) {
                         throw new EmailSendFail();
                     }
                 },
                UserNotFound::new
        );
    }
    @GetMapping("/verify/{uidb64}/{token}")
    public RedirectView verifyEmailPermit(@PathVariable String token, @PathVariable String uidb64 )  {
        return emailService.verifyEmailAndRedirect(token,uidb64);
    }
    @PostMapping("/token/refresh/")
    public JwtResponseDTO refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO){
        UserInfo user = userService.findUserByRefresh(refreshTokenRequestDTO.getRefresh());
        if (jwtService.verifyRefreshTokenExpiration(user.getRefreshToken())) {
            String newAccessToken = jwtService.generateToken(user.getEmail(), String.valueOf(user.getId()));
            return JwtResponseDTO.builder()
                    .access(newAccessToken)
                    .refresh(refreshTokenRequestDTO.getRefresh()).build();
        } else {
            throw new RefreshTokenExpired();
        }
    }
    @PutMapping("/find-password/")
    public UserNewPasswordResponseDTO findPassword(@RequestBody EmailRequestDTO emailRequestDTO){
        UserInfo user = userService.getIfUserByEmail(emailRequestDTO.getEmail()).orElseThrow(UserNotFound::new);
        return userService.findPassword(user);
    }
    @PutMapping("/{uid}/")
    public void editUser(HttpServletRequest request, @RequestBody EditUserInfoRequestDTO editUserInfoRequestDTO, @PathVariable Long uid){
        UserInfo user =userService.getUserByRequest(request).orElseThrow(UserNotFound::new);
        if (Objects.equals(user.getId(), uid)) {
            userService.changeUserInfo(user, editUserInfoRequestDTO);
        } else {
            throw new AccessDenied();
        }
    }
    @DeleteMapping("/{uid}/")
    public void deleteUser(HttpServletRequest request,@PathVariable Long uid) {
        UserInfo user =userService.getUserByRequest(request).orElseThrow(UserNotFound::new);
        if (Objects.equals(user.getId(), uid)) {
            userService.deleteUser(user);
        } else {
            throw new AccessDenied();
        }
    }
    @PostMapping("/{uid}/image/")
    public ImageResponseDTO editUserImage(HttpServletRequest request,@PathVariable Long uid, @RequestBody MultipartFile image) throws IOException {
        UserInfo user =userService.getUserByRequest(request).orElseThrow(UserNotFound::new);
            if (Objects.equals(user.getId(), uid)) {
                return userService.saveUserImage(user,image);
            } else {
                throw new AccessDenied();
            }
        }
    }



