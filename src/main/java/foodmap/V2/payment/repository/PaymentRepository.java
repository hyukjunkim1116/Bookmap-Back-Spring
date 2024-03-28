package foodmap.V2.payment.repository;

import foodmap.V2.payment.domain.Payment;

import foodmap.V2.user.domain.UserInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PaymentRepository  extends JpaRepository<Payment, Long> {
     List<Payment> findAllByBuyer(UserInfo user);
}
