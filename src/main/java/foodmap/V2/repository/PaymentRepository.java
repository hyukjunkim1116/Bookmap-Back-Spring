package foodmap.V2.repository;

import foodmap.V2.domain.Payment;
import foodmap.V2.domain.Report;
import foodmap.V2.domain.UserInfo;
import foodmap.V2.dto.response.PaymentResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository  extends JpaRepository<Payment, Long> {
    public List<Payment> findAllByBuyer(UserInfo user);
}
