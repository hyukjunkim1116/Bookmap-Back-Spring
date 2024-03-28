package foodmap.V2.payment.dto;

import lombok.Builder;
import lombok.Data;

@Data // 롬복을 사용하여 Getter, Setter, toString 등의 메서드를 자동으로 생성하는 어노테이션
@Builder // 빌더 패턴을 적용하는 어노테이션
public class PaymentReqeustDTO {
    private Long buyer;
    private Long amount;
    private String imp;
    private String merchant;
}
