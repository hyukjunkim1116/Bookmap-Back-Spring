package foodmap.V2.exception.user;

import foodmap.V2.exception.FoodMapException;
import lombok.Getter;

/**
 * status -> 400
 */
@Getter
public class InvalidRequest extends FoodMapException {

    private static final String MESSAGE = "잘못된 요청입니다.";

    public InvalidRequest() {
        super(MESSAGE);
    }

    public InvalidRequest(String fieldName, String message) {
        super(MESSAGE);
        addValidation(fieldName, message);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
