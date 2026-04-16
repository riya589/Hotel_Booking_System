package com.hotelbooking.service;

import com.hotelbooking.dto.PaymentRequest;
import com.hotelbooking.dto.PaymentResponse;
import com.hotelbooking.exception.BadRequestException;
import com.hotelbooking.exception.ResourceNotFoundException;
import com.hotelbooking.exception.UnauthorizedActionException;
import com.hotelbooking.model.Booking;
import com.hotelbooking.model.BookingStatus;
import com.hotelbooking.model.Payment;
import com.hotelbooking.model.PaymentStatus;
import com.hotelbooking.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingService bookingService;
    private final BillingService billingService;

    @Transactional
    public PaymentResponse processPayment(Long bookingId, Long userId, boolean admin, PaymentRequest request) {
        if (!bookingId.equals(request.getBookingId())) {
            throw new BadRequestException("Booking ID in the request does not match the payment route");
        }

        Booking booking = admin
                ? bookingService.getBookingEntity(bookingId)
                : bookingService.getBookingEntity(bookingService.getBooking(bookingId, userId, false).getBookingId());

        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Cancelled bookings cannot be paid");
        }
        if (booking.getPaymentStatus() == PaymentStatus.SUCCESS) {
            throw new BadRequestException("Payment has already been completed for this booking");
        }

        var bill = billingService.getBillEntity(bookingId);
        if (bill.getFinalAmount().compareTo(request.getAmount()) != 0) {
            throw new BadRequestException("Payment amount does not match the invoice amount");
        }

        Payment payment = paymentRepository.findByBookingId(bookingId).orElseGet(Payment::new);
        payment.setBooking(booking);
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(normalizeMethod(request.getPaymentMethod()));
        payment.setRemarks("Demo payment processed on " + LocalDateTime.now());

        boolean successful = isPaymentSuccessful(request);
        if (successful) {
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase(Locale.ROOT));
            booking.setPaymentStatus(PaymentStatus.SUCCESS);
            booking.setBookingStatus(BookingStatus.CONFIRMED);
        } else {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            booking.setPaymentStatus(PaymentStatus.FAILED);
            booking.setBookingStatus(BookingStatus.PENDING);
        }

        paymentRepository.save(payment);
        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .bookingId(booking.getId())
                .paymentStatus(payment.getPaymentStatus().name())
                .transactionId(payment.getTransactionId())
                .paymentDate(payment.getPaymentDate())
                .message(successful ? "Payment successful. Booking confirmed." : "Payment failed. Please verify details and try again.")
                .build();
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentForBooking(Long bookingId, Long userId, boolean admin) {
        if (!admin) {
            bookingService.getBooking(bookingId, userId, false);
        }
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for this booking"));

        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .bookingId(payment.getBooking().getId())
                .paymentStatus(payment.getPaymentStatus().name())
                .transactionId(payment.getTransactionId())
                .paymentDate(payment.getPaymentDate())
                .message("Payment details fetched successfully")
                .build();
    }

    private String normalizeMethod(String paymentMethod) {
        if (paymentMethod == null) {
            throw new BadRequestException("Payment method is required");
        }
        return paymentMethod.trim().toUpperCase(Locale.ROOT);
    }

    private boolean isPaymentSuccessful(PaymentRequest request) {
        String method = normalizeMethod(request.getPaymentMethod());
        return switch (method) {
            case "CARD" -> request.getCardNumber() != null && request.getCardNumber().matches("^[0-9]{16}$");
            case "UPI" -> true;
            case "CASH" -> true;
            default -> throw new BadRequestException("Unsupported payment method. Use CARD, UPI, or CASH.");
        };
    }
}
