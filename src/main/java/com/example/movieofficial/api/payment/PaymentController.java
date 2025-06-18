package com.example.movieofficial.api.payment;

import com.example.movieofficial.api.payment.dtos.PaymentInfo;
import com.example.movieofficial.api.payment.method.PaymentMethod;
import com.example.movieofficial.api.payment.services.PaymentService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<List<PaymentInfo>> getAll() {
        return ResponseEntity.ok(paymentService.getAll());
    }

    @GetMapping("/redirect-url")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<String> getPaymentUrlByBill(
            @RequestParam(name = "billId") String billId,
            @RequestParam(name = "method") PaymentMethod paymentMethod,
            HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization");
        return ResponseEntity.ok(paymentService.createPaymentUrl(token, billId, paymentMethod));
    }

//    @GetMapping("/callback/vnpay")
//    @Hidden
//    public ResponseEntity<Void> handlePayment(
//            @RequestParam("vnp_Amount") String amount,
//            @RequestParam("vnp_BankCode") String bankCode,
//            @RequestParam("vnp_BankTranNo") String bankTranNo,
//            @RequestParam("vnp_CardType") String cardType,
//            @RequestParam("vnp_OrderInfo") String orderInfo,
//            @RequestParam("vnp_PayDate") String payDate,
//            @RequestParam("vnp_ResponseCode") String responseCode,
//            @RequestParam("vnp_TmnCode") String tmnCode,
//            @RequestParam("vnp_TransactionNo") String transactionNo,
//            @RequestParam("vnp_TransactionStatus") String transactionStatus,
//            @RequestParam("vnp_TxnRef") String txnRef,
//            @RequestParam("vnp_SecureHash") String secureHash
//    ) {
//        String redirect = paymentService.handleVnPay(txnRef, responseCode, transactionStatus, payDate);
//        return ResponseEntity.status(302)
//                .location(URI.create(redirect))
//                .build();
//    }

    @GetMapping("/callback/vnpay")
    @Hidden
    public ResponseEntity<Void> handlePayment(
            HttpServletRequest request
    ) {
        String redirect = paymentService.handleVnPay(request);
        return ResponseEntity.status(302)
                .location(URI.create(redirect))
                .build();
    }
}
