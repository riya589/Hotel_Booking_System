package com.hotelbooking.service;

import com.hotelbooking.dto.BillResponse;
import com.hotelbooking.exception.ResourceNotFoundException;
import com.hotelbooking.model.Bill;
import com.hotelbooking.model.Booking;
import com.hotelbooking.repository.BillRepository;
import com.hotelbooking.util.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class BillingService {
    private static final BigDecimal TAX_RATE = new BigDecimal("0.12");
    private static final BigDecimal SERVICE_CHARGE_RATE = new BigDecimal("0.05");

    private final BillRepository billRepository;

    @Transactional
    public Bill generateOrUpdateBill(Booking booking) {
        BigDecimal baseAmount = booking.getRoom().getPricePerNight()
                .multiply(BigDecimal.valueOf(booking.getTotalNights()))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal taxAmount = baseAmount.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal serviceCharge = baseAmount.multiply(SERVICE_CHARGE_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal discount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        BigDecimal finalAmount = baseAmount.add(taxAmount).add(serviceCharge).subtract(discount).setScale(2, RoundingMode.HALF_UP);

        Bill bill = billRepository.findByBookingId(booking.getId()).orElseGet(Bill::new);
        bill.setBooking(booking);
        bill.setBaseAmount(baseAmount);
        bill.setTaxAmount(taxAmount);
        bill.setServiceCharge(serviceCharge);
        bill.setDiscountAmount(discount);
        bill.setFinalAmount(finalAmount);
        return billRepository.save(bill);
    }

    @Transactional(readOnly = true)
    public BillResponse getBillForBooking(Long bookingId) {
        Bill bill = billRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found for this booking"));
        return DtoMapper.toBillResponse(bill);
    }

    @Transactional(readOnly = true)
    public Bill getBillEntity(Long bookingId) {
        return billRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found for this booking"));
    }
}
