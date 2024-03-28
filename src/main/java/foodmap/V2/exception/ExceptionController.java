package foodmap.V2.exception;

import com.amazonaws.AmazonClientException;
import com.amazonaws.SdkClientException;
import foodmap.V2.exception.ErrorResponse;
import foodmap.V2.exception.FoodMapException;
import foodmap.V2.exception.jwt.CustomExpiredJwtException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

@Slf4j
@ControllerAdvice
public class ExceptionController {
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomExpiredJwtException.class)
    public ErrorResponse CustomExpiredJwtExceptionHandler(CustomExpiredJwtException e) {
        int statusCode = e.getStatusCode();
        return ErrorResponse.builder()
                .code(String.valueOf(statusCode))
                .message(e.getMessage())
                .build();
    }
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        ErrorResponse response = ErrorResponse.builder()
                .code("400")
                .message("잘못된 요청입니다.")
                .build();
        for (FieldError fieldError : e.getFieldErrors()) {
            response.addValidation(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return response;
    }
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadCredentialsException.class)
    public ErrorResponse BadCredentialsExceptionHandler(BadCredentialsException e) {
        return ErrorResponse.builder()
                .code("400")
                .message("아이디 혹은 비밀번호가 틀렸습니당")
                .build();
    }
    @ResponseBody
    @ExceptionHandler(FoodMapException.class)
    public ResponseEntity<ErrorResponse> FoodMapExceptionHandler(FoodMapException e) {
        int statusCode = e.getStatusCode();
        ErrorResponse body = ErrorResponse.builder()
                .code(String.valueOf(statusCode))
                .message(e.getMessage())
                .validation(e.getValidation())
                .build();
        return ResponseEntity.status(statusCode)
                .body(body);
    }
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SdkClientException.class)
    public ErrorResponse SdkClientExceptionHandler(SdkClientException e) {
        return ErrorResponse.builder()
                .code("400")
                .message("Sdk Client Exception 발생: " + e.getMessage())
                .build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AmazonClientException.class)
    public ErrorResponse AmazonClientExceptionHandler(AmazonClientException e) {
        return ErrorResponse.builder()
                .code("400")
                .message("AmazonClientException 발생: "+e.getMessage())
                .build();
    }
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IOException.class)
    public ErrorResponse IOExceptionHandler(IOException e) {
        return ErrorResponse.builder()
                .code("400")
                .message("IOException 발생: "+e.getMessage())
                .build();
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exception(Exception e) {
        log.error("예외발생", e);
        ErrorResponse body = ErrorResponse.builder()
                .code("500")
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(500)
                .body(body);
    }
}
