package foodmap.V2.controller;


import foodmap.V2.config.kakao.*;
import foodmap.V2.dto.response.ImageResponseDTO;
import foodmap.V2.dto.response.user.UserCreateResponse;
import foodmap.V2.repository.UserRepository;
import foodmap.V2.domain.RefreshToken;
import foodmap.V2.domain.UserInfo;
import foodmap.V2.dto.request.*;
import foodmap.V2.dto.response.JwtResponseDTO;
import foodmap.V2.exception.user.UserNotFound;
import foodmap.V2.service.EmailService;
import foodmap.V2.service.JwtService;
import foodmap.V2.service.RefreshTokenService;
import foodmap.V2.service.UserService;
import jakarta.mail.MessagingException;
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
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final KakaoTokenJsonData kakaoTokenJsonData;
    private final KakaoUserInfo kakaoUserInfo;
    private final KakaoService kakaoService;
    @GetMapping("/favicon.ico")
    public void returnEmptyFavicon() {
        // 아무런 내용도 반환하지 않음
    }
    @PostMapping("/")
    public void signup(@RequestBody SignUpRequestDTO signUpRequestDTO){
        userService.signup(signUpRequestDTO);
    }

    @PostMapping("/login/")
    public UserCreateResponse AuthenticateAndGetToken(@RequestBody LoginRequestDTO loginRequestDTO){
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword()));
        if(authentication.isAuthenticated()){
            UserInfo user = userService.getUserByEmail(loginRequestDTO.getEmail());
            var token = refreshTokenService.findByUser(user);
            token.ifPresent(refreshTokenService::deleteToken);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(loginRequestDTO.getEmail());
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
            throw new UsernameNotFoundException("invalid user request..!!");
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
            var token = refreshTokenService.findByUser(user);
            token.ifPresent(refreshTokenService::deleteToken);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser.getEmail());
            return UserCreateResponse.builder()
                    .is_verified(user.getIsVerified())
                    .social(user.getSocial())
                    .username(user.getUsername())
                    .image(user.getImage())
                    .email(user.getEmail())
                    .access(JwtService.GenerateToken(user.getEmail(), String.valueOf(user.getId())))
                    .refresh(refreshToken.getRefresh())
                    .build();
        } else {
            throw new UsernameNotFoundException("invalid user request..!!");
        }
    }
    @PutMapping("/change-password/")
    public void changePassword(@RequestHeader("Authorization") String token, @RequestBody ChangePasswordRequestDTO changePasswordRequestDTO){
        String jwtToken = token.substring(7);
        var email = jwtService.extractUsername(jwtToken);
        var user = userService.getUserByEmail(email);
        userService.authChangePassword(user,changePasswordRequestDTO);
    }
    @PostMapping("/{uid}/verify/")
    public void sendVerifyEmail(@RequestHeader("Authorization") String token, @PathVariable Long uid) throws MessagingException {
        String accessToken = token.substring(7);
        var email = jwtService.extractUsername(accessToken);
        String uidb64= Base64.getEncoder().encodeToString(String.valueOf(uid).getBytes());
        // userId를 문자열로 변환하여 Base64로 인코딩
        String testToken = "test";

        emailService.sendHtmlEmail(email,"FoodMap 인증 메일", uidb64,testToken);
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
    //localhost:9030/api/users/
    @PostMapping("/token/refresh/")
    public JwtResponseDTO refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO){

        return refreshTokenService.findByToken(refreshTokenRequestDTO.getRefresh())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserInfo)
                .map(userInfo -> {
                    String accessToken = JwtService.GenerateToken(userInfo.getUsername(), String.valueOf(userInfo.getId()));
                    return JwtResponseDTO.builder()
                            .access(accessToken)
                            .refresh(refreshTokenRequestDTO.getRefresh()).build();
                }).orElseThrow(() ->new RuntimeException("Refresh Token is not in DB..!!"));
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
    public void editUser(@RequestBody EditUserInfoDTO editUserInfoDTO, @PathVariable Long uid){
        log.info("uid,{}",uid);
        userService.changeUserInfo(uid,editUserInfoDTO);
    };
    @DeleteMapping("/{uid}/")
    public void deleteUser(@PathVariable Long uid){

        Optional<UserInfo> userOptional = userService.getUserById(uid);
        if (userOptional.isPresent()) {
            UserInfo user = userOptional.get();
            var token = refreshTokenService.findByUser(user);
            token.ifPresent(refreshTokenService::deleteToken);
            userService.deleteUser(user);
        } else {
            throw new UserNotFound();
        }
        }

    @PatchMapping("/{uid}/image/")
    public ImageResponseDTO editUserImage(@PathVariable Long uid, @RequestBody MultipartFile image) throws IOException {
        log.info("uid,{}",uid);
        log.info("image,{}",image);
        String newImage =  userService.saveUserImage(uid,image);
        return ImageResponseDTO.builder()
                .image(newImage)
                .build();
    }
    };
//    @PreAuthorize("hasAuthority('ADMIN')")
//    @GetMapping("/api/ping")
//    public String Test() {
//        try {
//            return "Welcome";
//        } catch (Exception e){
//            throw new RuntimeException(e);
//        }
//    }



