package foodmap.V2.dto.response;

import foodmap.V2.domain.Payment;
import foodmap.V2.domain.UserInfo;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PaymentResponse {
    private final Long id;
    private final String imp;
    private final String merchant;
    private final String amount;
    private final Long buyer;
    private final LocalDateTime createdAt;
    public PaymentResponse(Payment payment) {
        this.id = payment.getId();
        this.buyer = payment.getBuyer().getId();
        this.createdAt = payment.getCreatedAt();
        this.merchant = payment.getMerchant();
        this.amount = payment.getAmount();
        this.imp = payment.getImp();
    }
    @Builder
    public PaymentResponse(Long id, String imp, String merchant, String amount, Long buyer, LocalDateTime createdAt) {
        this.id = id;
        this.imp = imp;
        this.merchant = merchant;
        this.amount = amount;
        this.buyer = buyer;
        this.createdAt = createdAt;
    }
}