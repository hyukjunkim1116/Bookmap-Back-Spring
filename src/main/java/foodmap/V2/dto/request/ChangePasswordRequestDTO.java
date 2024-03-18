package foodmap.V2.dto.request;

import lombok.Builder;
import lombok.Data;

@Data // 롬복을 사용하여 Getter, Setter, toString 등의 메서드를 자동으로 생성하는 어노테이션
@Builder // 빌더 패턴을 적용하는 어노테이션
public class ChangePasswordRequestDTO {
    private String oldPassword;
    private String newPassword; // 사용자 비밀번호
    private String newPasswordConfirm; // 사용자 비밀번호 확인
}
