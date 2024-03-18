package foodmap.V2.service;

import foodmap.V2.repository.RefreshTokenRepository;
import foodmap.V2.repository.UserRepository;
import foodmap.V2.domain.RefreshToken;
import foodmap.V2.domain.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshToken createRefreshToken(String email){
        RefreshToken refreshToken = RefreshToken.builder()
                .userInfo(userRepository.findByEmail(email))
                .refresh(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(1000*60*60)) // set expiry of refresh token to 10 minutes - you can configure it application.properties file
                .build();
        log.info(String.valueOf(refreshToken));
        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByUser(UserInfo userInfo){
        return refreshTokenRepository.findRefreshTokenByUserInfo(userInfo);
    }
    public void deleteToken(RefreshToken token) {
        log.info("token,{}",token);
        refreshTokenRepository.delete(token);
    }

    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByRefresh(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token){
        if(token.getExpiryDate().compareTo(Instant.now())<0){
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getRefresh() + " Refresh token is expired. Please make a new login..!");
        }
        return token;
    }

}
