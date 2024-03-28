package foodmap.V2.payment.domain;

import foodmap.V2.user.domain.UserInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn
    private UserInfo buyer;
    private LocalDateTime createdAt;
    private String amount;
    private String imp;
    private String merchant;

    // 생성자 오버로딩

    @Builder
    public Payment(String amount, String imp, String merchant, UserInfo buyer) {
        this.amount = amount;
        this.imp = imp;
        this.merchant = merchant;
        this.buyer = buyer;
        this.createdAt = LocalDateTime.now();
    }
}

