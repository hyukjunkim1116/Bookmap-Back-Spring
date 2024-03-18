package foodmap.V2.dto.request;
import lombok.Builder;
import lombok.Data;

@Data // 롬복을 사용하여 Getter, Setter, toString 등의 메서드를 자동으로 생성하는 어노테이션
@Builder // 빌더 패턴을 적용하는 어노테이션
public class SignUpRequestDTO {
    private String email; // 사용자 이메일
    private String password; // 사용자 비밀번호
    private String passwordConfirm; // 사용자 비밀번호 확인
    private String username; // 사용자 이름
}
