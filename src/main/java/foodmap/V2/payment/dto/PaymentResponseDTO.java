package foodmap.V2.payment.dto;

import foodmap.V2.payment.domain.Payment;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PaymentResponseDTO {
    private final Long id;
    private final String imp;
    private final String merchant;
    private final String amount;
    private final Long buyer;
    private final LocalDateTime createdAt;
    public PaymentResponseDTO(Payment payment) {
        this.id = payment.getId();
        this.buyer = payment.getBuyer().getId();
        this.createdAt = payment.getCreatedAt();
        this.merchant = payment.getMerchant();
        this.amount = payment.getAmount();
        this.imp = payment.getImp();
    }
    @Builder
    public PaymentResponseDTO(Long id, String imp, String merchant, String amount, Long buyer, LocalDateTime createdAt) {
        this.id = id;
        this.imp = imp;
        this.merchant = merchant;
        this.amount = amount;
        this.buyer = buyer;
        this.createdAt = createdAt;
    }
}