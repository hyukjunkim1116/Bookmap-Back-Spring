package foodmap.V2.controller;


import foodmap.V2.dto.response.KakaoUserCredentials;
import foodmap.V2.dto.response.KakaoUserInfo;
import foodmap.V2.dto.response.ImageResponseDTO;
import foodmap.V2.dto.response.KakaoTokenResponse;
import foodmap.V2.dto.response.KakaoUserInfoResponse;
import foodmap.V2.dto.response.user.UserCreateResponse;
import foodmap.V2.domain.RefreshToken;
import foodmap.V2.domain.UserInfo;
import foodmap.V2.dto.request.*;
import foodmap.V2.dto.response.JwtResponseDTO;
import foodmap.V2.exception.user.AccessDenied;
import foodmap.V2.exception.user.RefreshTokenExpired;
import foodmap.V2.exception.user.InvalidRequest;
import foodmap.V2.exception.user.UserNotFound;
import foodmap.V2.service.EmailService;
import foodmap.V2.service.JwtService;
import foodmap.V2.service.user.UserService;
import foodmap.V2.unused.KakaoService;
import foodmap.V2.utils.KakaoTokenJsonData;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Base64;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final KakaoTokenJsonData kakaoTokenJsonData;
    private final KakaoUserInfo kakaoUserInfo;
    private final KakaoService kakaoService;
    @PostMapping("/")
    public void signup(@RequestBody SignUpRequestDTO signUpRequestDTO){
        userService.signup(signUpRequestDTO);
    }
    @PostMapping("/login/")
    public UserCreateResponse AuthenticateAndGetToken(@RequestBody LoginRequestDTO loginRequestDTO){
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword()));
        if(authentication.isAuthenticated()){
            UserInfo user = userService.getUserByEmail(loginRequestDTO.getEmail());
            RefreshToken refreshToken = jwtService.createRefreshToken();
            userService.changeUserRefreshToken(user,refreshToken);
            return UserCreateResponse.builder()
                    .is_verified(user.getIsVerified())
                    .social(user.getSocial())
                    .username(user.getUsername())
                    .image(user.getImage())
                    .email(user.getEmail())
                    .access(JwtService.GenerateToken(loginRequestDTO.getEmail(), String.valueOf(user.getId())))
                    .refresh(refreshToken.getRefresh())
                    .build();
        } else {
            throw new InvalidRequest();
        }
    }
    @PostMapping("/kakao/")
    public UserCreateResponse kakaoLogin(@RequestBody String code) {
        KakaoTokenResponse kakaoTokenResponse = kakaoTokenJsonData.getData(code);
        KakaoUserInfoResponse userInfo = kakaoUserInfo.getUserInfo(kakaoTokenResponse.getAccess_token());
        KakaoUserCredentials savedUser = userService.kakaoSignUp(userInfo);
//        kakaoService.sendKakaoMessage(kakaoTokenResponse.getAccess_token());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(new UsernamePasswordAuthenticationToken(savedUser.getEmail(), savedUser.getPassword()));
        if(authentication.isAuthenticated()){
            UserInfo user = userService.getUserByEmail(savedUser.getEmail());
            RefreshToken newRefreshToken = jwtService.createRefreshToken();
            userService.changeUserRefreshToken(user,newRefreshToken);
            return UserCreateResponse.builder()
                    .is_verified(user.getIsVerified())
                    .social(user.getSocial())
                    .username(user.getUsername())
                    .image(user.getImage())
                    .email(user.getEmail())
                    .access(JwtService.GenerateToken(user.getEmail(), String.valueOf(user.getId())))
                    .refresh(newRefreshToken.getRefresh())
                    .build();
        } else {
            throw new UsernameNotFoundException("invalid user request..!!");
        }
    }
    @PutMapping("/change-password/")
    public void changePassword(HttpServletRequest request, @RequestBody ChangePasswordRequestDTO changePasswordRequestDTO){
        Optional<UserInfo> user =userService.getUserByRequest(request);
        if (user.isPresent()) {
            userService.authChangePassword(user.get(),changePasswordRequestDTO);
        } else {
            throw new UserNotFound();
        }

    }
    @PostMapping("/{uid}/verify/")
    public void sendVerifyEmail(HttpServletRequest request,@PathVariable Long uid) throws MessagingException {
        Optional<UserInfo> user =userService.getUserByRequest(request);
        if (user.isPresent()) {
            String uidb64= Base64.getEncoder().encodeToString(String.valueOf(uid).getBytes());
            // userId를 문자열로 변환하여 Base64로 인코딩
            String testToken = "test";
            String email = user.get().getEmail();
            emailService.sendHtmlEmail(email,"FoodMap 인증 메일", uidb64,testToken);
        } else {
            throw new UserNotFound();
        }
    }
    @GetMapping("/verify/{uidb64}/{token}")
    public RedirectView verifyEmailPermit(@PathVariable String token, @PathVariable String uidb64 )  {
        RedirectView redirectView = new RedirectView();
        // 문자열 디코딩
        byte[] decodedBytes = Base64.getDecoder().decode(uidb64);
        String decodedUid = new String(decodedBytes);
        log.info("uid,{}",decodedUid);
        Optional<UserInfo> userInfoOptional = userService.getUserById(Long.valueOf(decodedUid));
        userInfoOptional.ifPresentOrElse(userService::verifyUser, UserNotFound::new);
        redirectView.setUrl(String.format("http://localhost:9030/verify/%s", token));
        return redirectView;
    }
    @PostMapping("/token/refresh/")
    public JwtResponseDTO refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO){
        UserInfo user = userService.findUserByRefresh(refreshTokenRequestDTO.getRefresh());
        if (jwtService.verifyRefreshTokenExpiration(user.getRefreshToken())) {
            String newAccessToken = JwtService.GenerateToken(user.getEmail(), String.valueOf(user.getId()));
            return JwtResponseDTO.builder()
                    .access(newAccessToken)
                    .refresh(refreshTokenRequestDTO.getRefresh()).build();
        } else {
            throw new RefreshTokenExpired();
        }
    }
    @PutMapping("/find-password/")
    public ResponseEntity<Object> findPassword(@RequestBody EmailRequestDTO emailRequestDTO){
        Optional<UserInfo> user = userService.getIfUserByEmail(emailRequestDTO.getEmail());
        String newPassword = userService.getTempPassword();
        user.ifPresentOrElse(userInfo ->
               userService.changePassword(userInfo,newPassword),
                UserNotFound::new);
        var result = new HashMap<>();
        result.put("password",newPassword);
        return ResponseEntity.ok().body(result);
    }
    @PutMapping("/{uid}/")
    public void editUser(HttpServletRequest request,@RequestBody EditUserInfoDTO editUserInfoDTO, @PathVariable Long uid){
        Optional<UserInfo> user =userService.getUserByRequest(request);
        if (user.isPresent()) {
            log.info("user123:{}",user.get().getId());
            if (Objects.equals(user.get().getId(), uid)) {
                userService.changeUserInfo(user.get(),editUserInfoDTO);
            } else {
                throw new AccessDenied();
            }
        } else {
            throw new UserNotFound();
        }


    }
    @DeleteMapping("/{uid}/")
    public void deleteUser(HttpServletRequest request,@PathVariable Long uid) {
        Optional<UserInfo> user = userService.getUserByRequest(request);
        if (user.isPresent()) {
            if (Objects.equals(user.get().getId(), uid)) {
                userService.deleteUser(user.get());
            } else {
                throw new AccessDenied();
            }
        } else {
            throw new UserNotFound();
        }
    }

    @PostMapping("/{uid}/image/")
    public ImageResponseDTO editUserImage(HttpServletRequest request,@PathVariable Long uid, @RequestBody MultipartFile image) throws IOException {
        Optional<UserInfo> user = userService.getUserByRequest(request);
        if (user.isPresent()) {
            if (Objects.equals(user.get().getId(), uid)) {
                return userService.saveUserImage(uid,image);
            } else {
                throw new AccessDenied();
            }
        } else {
            throw new UserNotFound();
        }
    }
    }



