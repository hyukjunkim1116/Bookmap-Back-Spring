package foodmap.V2.exception.user;

import foodmap.V2.exception.FoodMapException;

/**
 * status -> 401
 */
public class Unauthorized extends FoodMapException {

    private static final String MESSAGE = "인증이 필요합니다.";
    public Unauthorized() {
        super(MESSAGE);
    }
    @Override
    public int getStatusCode() {
        return 401;
    }
}
