package foodmap.V2.exception.user;

import foodmap.V2.exception.FoodMapException;

public class InvalidPassword extends FoodMapException {

    private static final String MESSAGE = "비밀번호가 올바르지 않습니다.";

    public InvalidPassword() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
