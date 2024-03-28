package foodmap.V2.util.email;
import foodmap.V2.exception.user.UserNotFound;
import foodmap.V2.user.domain.UserInfo;
import foodmap.V2.user.service.UserService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final UserService userService;
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final static String FRONT_END_REDIRECT_URL = "http://localhost:9030/verify/";
    public void sendHtmlEmail(String to,Long uid) throws MessagingException {
        String uidb64 = this.encodeUid(uid);
        String subject = "FoodMap 인증 메일";
        String testToken = "test";
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        Context context = new Context();
        context.setVariable("uidb64", uidb64);
        context.setVariable("token", testToken);
        String htmlContent = templateEngine.process("email", context);
        helper.setText(htmlContent, true);
        javaMailSender.send(message);
    }
    public RedirectView verifyEmailAndRedirect(String token,String uidb64) {
        RedirectView redirectView = new RedirectView();
        byte[] decodedBytes = Base64.getDecoder().decode(uidb64);
        String decodedUid = new String(decodedBytes);
        Optional<UserInfo> userInfoOptional = userService.getUserById(Long.valueOf(decodedUid));
        userInfoOptional.ifPresentOrElse(userService::verifyUser, UserNotFound::new);
        redirectView.setUrl(FRONT_END_REDIRECT_URL+token);
        return redirectView;
    }
    public String encodeUid(Long uid) {
        return Base64.getEncoder().encodeToString(String.valueOf(uid).getBytes());
    }
}