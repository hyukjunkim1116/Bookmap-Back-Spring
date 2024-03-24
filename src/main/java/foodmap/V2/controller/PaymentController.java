package foodmap.V2.controller;


import foodmap.V2.dto.request.PaymentReqeustDTO;
import foodmap.V2.dto.response.PaymentResponse;
import foodmap.V2.exception.user.UserNotFound;
import foodmap.V2.service.JwtService;
import foodmap.V2.service.PaymentService;
import foodmap.V2.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentController {
    private final UserService userService;
    private final JwtService jwtService;
    private final PaymentService paymentService;
    @PostMapping("/api/payments/pay")
    public void create(@RequestBody PaymentReqeustDTO paymentReqeustDTO){
        paymentService.write(paymentReqeustDTO);
    }
    @GetMapping("/api/payments/pay")
    public List<PaymentResponse> get(@RequestHeader("Authorization") String token){
        String jwtToken = token.substring(7);
        String userId = jwtService.extractUserid(jwtToken);
        return paymentService.getList(Long.valueOf(userId));
    }
}
