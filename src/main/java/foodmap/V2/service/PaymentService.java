package foodmap.V2.service;

import foodmap.V2.domain.Payment;
import foodmap.V2.domain.UserInfo;
import foodmap.V2.dto.request.PaymentReqeustDTO;
import foodmap.V2.dto.response.PaymentResponse;
import foodmap.V2.exception.user.UserNotFound;
import foodmap.V2.repository.PaymentRepository;
import foodmap.V2.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final UserService userService;
    private final PaymentRepository paymentRepository;
    @Transactional
    public void write( PaymentReqeustDTO request) {
        log.info("asd,{}",request.getBuyer());
        UserInfo user =userService.getUserById(request.getBuyer()).orElseThrow(UserNotFound::new);
        Payment payment = Payment.builder()
                .merchant(request.getMerchant())
                .imp(request.getImp())
                .buyer(user)
                .amount(String.valueOf(request.getAmount()))
                .build();
        paymentRepository.save(payment);
    }
    public List<PaymentResponse> getList(Long userId) {
        UserInfo user =userService.getUserById(userId).orElseThrow(UserNotFound::new);
        return paymentRepository.findAllByBuyer(user).stream()
                .map(PaymentResponse::new)
                .collect(Collectors.toList());
    }

}
