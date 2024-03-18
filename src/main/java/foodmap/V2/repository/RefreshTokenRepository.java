package foodmap.V2.repository;

import foodmap.V2.domain.RefreshToken;
import foodmap.V2.domain.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByRefresh(String token);
    Optional<RefreshToken> findRefreshTokenByUserInfo(UserInfo userInfo);

}
