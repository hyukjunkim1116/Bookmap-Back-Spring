package foodmap.V2.repository;

import foodmap.V2.domain.Payment;

import foodmap.V2.domain.UserInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PaymentRepository  extends JpaRepository<Payment, Long> {
     List<Payment> findAllByBuyer(UserInfo user);
}
