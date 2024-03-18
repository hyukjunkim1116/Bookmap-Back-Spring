package foodmap.V2.repository;

import foodmap.V2.domain.Chat;
import foodmap.V2.domain.Notification;
import foodmap.V2.domain.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    public List<Notification> findAllByReciever(Long userId);
}
