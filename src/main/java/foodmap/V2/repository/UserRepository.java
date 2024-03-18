package foodmap.V2.repository;

import foodmap.V2.domain.UserInfo;
import foodmap.V2.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserInfo, Long> {
    public UserInfo findByEmail(String email);
    Optional<UserInfo> findUserInfoByEmail(String email);
}
