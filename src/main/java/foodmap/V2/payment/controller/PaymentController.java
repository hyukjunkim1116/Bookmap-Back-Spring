package foodmap.V2.payment.controller;


import foodmap.V2.exception.user.UserNotFound;
import foodmap.V2.payment.dto.PaymentReqeustDTO;
import foodmap.V2.payment.dto.PaymentResponseDTO;
import foodmap.V2.payment.service.PaymentService;
import foodmap.V2.jwt.JwtService;
import foodmap.V2.user.domain.UserInfo;
import foodmap.V2.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final UserService userService;
    @PostMapping("/api/payments/pay")
    public void create(@RequestBody PaymentReqeustDTO paymentReqeustDTO){
        UserInfo user =userService.getUserById(paymentReqeustDTO.getBuyer()).orElseThrow(UserNotFound::new);
        paymentService.write(user,paymentReqeustDTO);
    }
    @GetMapping("/api/payments/pay")
    public List<PaymentResponseDTO> get(HttpServletRequest request){
        UserInfo user = userService.getUserByRequest(request).orElseThrow(UserNotFound::new);
        return paymentService.getList(user.getId());
    }
}
