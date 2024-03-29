package foodmap.V2.exception.jwt;

import foodmap.V2.exception.FoodMapException;

public class AccessTokenExpired extends FoodMapException {
    private static final String MESSAGE = "Access 토큰 만료";
    public AccessTokenExpired() {
        super(MESSAGE);
    }
    @Override
    public int getStatusCode() {
        return 401;
    }
}
