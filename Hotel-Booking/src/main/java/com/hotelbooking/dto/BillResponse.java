package com.hotelbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillResponse {
    private Long billId;
    private Long bookingId;
    private String hotelName;
    private String roomNumber;
    private String roomType;
    private String checkInDate;
    private String checkOutDate;
    private Integer totalNights;
    private BigDecimal baseAmount;
    private BigDecimal taxAmount;
    private BigDecimal serviceCharge;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private LocalDateTime generatedAt;
}
