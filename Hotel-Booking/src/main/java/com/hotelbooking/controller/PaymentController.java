package com.hotelbooking.controller;

import com.hotelbooking.dto.PaymentRequest;
import com.hotelbooking.dto.PaymentResponse;
import com.hotelbooking.security.UserDetailsImpl;
import com.hotelbooking.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/{bookingId}")
    public ResponseEntity<PaymentResponse> processPayment(@PathVariable Long bookingId,
                                                          @Valid @RequestBody PaymentRequest request,
                                                          Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        boolean admin = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return ResponseEntity.ok(paymentService.processPayment(bookingId, userDetails.getId(), admin, request));
    }

    @GetMapping("/{bookingId}")
    public PaymentResponse getPayment(@PathVariable Long bookingId, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        boolean admin = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return paymentService.getPaymentForBooking(bookingId, userDetails.getId(), admin);
    }
}
