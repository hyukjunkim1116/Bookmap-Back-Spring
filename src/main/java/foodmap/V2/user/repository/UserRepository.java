package foodmap.V2.user.repository;

import foodmap.V2.user.domain.UserInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserInfo, Long> {
    UserInfo findByEmail(String email);
    Optional<UserInfo> findUserInfoByEmail(String email);
    UserInfo findByRefreshTokenRefresh(String refresh);
}
