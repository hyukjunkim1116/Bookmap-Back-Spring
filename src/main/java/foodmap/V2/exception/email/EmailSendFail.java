package foodmap.V2.exception.email;

import foodmap.V2.exception.FoodMapException;

public class EmailSendFail extends FoodMapException {
    private static final String MESSAGE = "이메일 전송 실패";
    public EmailSendFail() {
        super(MESSAGE);
    }
    @Override
    public int getStatusCode() {
        return 403;
    }
}