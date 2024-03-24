package foodmap.V2.exception.user;

import foodmap.V2.exception.FoodMapException;

public class AccessDenied extends FoodMapException {

    private static final String MESSAGE = "접근 권한이 없습니당";

    public AccessDenied() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 403;
    }
}
