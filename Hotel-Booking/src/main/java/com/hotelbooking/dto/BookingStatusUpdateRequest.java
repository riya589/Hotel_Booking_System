package com.hotelbooking.dto;

import com.hotelbooking.model.BookingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingStatusUpdateRequest {
    @NotNull(message = "Booking status is required")
    private BookingStatus bookingStatus;
}
