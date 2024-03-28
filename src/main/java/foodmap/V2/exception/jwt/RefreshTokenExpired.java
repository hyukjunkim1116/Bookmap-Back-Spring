package foodmap.V2.exception.jwt;

import foodmap.V2.exception.FoodMapException;

/**
 * status -> 404
 */
public class RefreshTokenExpired extends FoodMapException {
    private static final String MESSAGE = "Refresh 토큰 만료";
    public RefreshTokenExpired() {
        super(MESSAGE);
    }
    @Override
    public int getStatusCode() {
        return 404;
    }
}