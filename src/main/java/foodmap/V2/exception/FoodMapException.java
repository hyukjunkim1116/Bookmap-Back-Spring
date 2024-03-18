package foodmap.V2.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter // 롬복을 사용하여 Getter 메서드를 자동으로 생성하는 어노테이션
public abstract class FoodMapException extends RuntimeException {

    // 예외 상황에서 발생한 유효성 검사 오류를 저장하기 위한 Map
    public final Map<String, String> validation = new HashMap<>();

    // 생성자
    public FoodMapException(String message) {
        super(message); // 부모 클래스인 RuntimeException의 생성자 호출
    }

    // 생성자
    public FoodMapException(String message, Throwable cause) {
        super(message, cause); // 부모 클래스인 RuntimeException의 생성자 호출
    }

    // 자식 클래스에서 구현할 추상 메서드로, 예외의 HTTP 상태 코드를 반환합니다.
    public abstract int getStatusCode();

    // 유효성 검사 오류를 추가하는 메서드
    public void addValidation(String fieldName, String message) {
        validation.put(fieldName, message); // validation Map에 오류 메시지 추가
    }
}
