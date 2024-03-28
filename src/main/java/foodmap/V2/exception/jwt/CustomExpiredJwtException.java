package foodmap.V2.exception.jwt;
import foodmap.V2.exception.FoodMapException;

public class CustomExpiredJwtException extends FoodMapException {

    private static final String MESSAGE = "엑세스 토큰 만료";
    public CustomExpiredJwtException() {
        super(MESSAGE);
    }
    @Override
    public int getStatusCode() {
        return 401;
    }
}
