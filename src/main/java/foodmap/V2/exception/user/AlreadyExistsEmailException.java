package foodmap.V2.exception.user;

import foodmap.V2.exception.FoodMapException;

// 이미 가입된 이메일로 회원가입을 시도할 때 발생하는 예외 클래스
public class AlreadyExistsEmailException extends FoodMapException {

    // 예외 메시지
    private static final String MESSAGE = "이미 가입된 이메일입니다.";

    // 생성자
    public AlreadyExistsEmailException() {
        // 부모 클래스의 생성자 호출하여 예외 메시지 전달
        super(MESSAGE);
    }

    // HTTP 상태 코드 반환 메서드
    @Override
    public int getStatusCode() {
        // 400 (Bad Request) 반환
        return 400;
    }
}
