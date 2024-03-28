package foodmap.V2.payment.service;

import foodmap.V2.payment.dto.PaymentReqeustDTO;
import foodmap.V2.payment.dto.PaymentResponseDTO;
import foodmap.V2.payment.domain.Payment;
import foodmap.V2.payment.repository.PaymentRepository;
import foodmap.V2.user.domain.UserInfo;
import foodmap.V2.exception.user.UserNotFound;
import foodmap.V2.user.service.UserService;
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
    public void write(UserInfo user,PaymentReqeustDTO request) {
        Payment payment = Payment.builder()
                .merchant(request.getMerchant())
                .imp(request.getImp())
                .buyer(user)
                .amount(String.valueOf(request.getAmount()))
                .build();
        paymentRepository.save(payment);
    }
    public List<PaymentResponseDTO> getList(Long userId) {
        UserInfo user =userService.getUserById(userId).orElseThrow(UserNotFound::new);
        return paymentRepository.findAllByBuyer(user).stream()
                .map(PaymentResponseDTO::new)
                .collect(Collectors.toList());
    }
}
