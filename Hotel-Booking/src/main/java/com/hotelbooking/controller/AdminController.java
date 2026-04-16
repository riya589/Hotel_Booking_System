package com.hotelbooking.controller;

import com.hotelbooking.dto.BookingResponse;
import com.hotelbooking.dto.BookingStatusUpdateRequest;
import com.hotelbooking.dto.DashboardResponse;
import com.hotelbooking.model.PaymentStatus;
import com.hotelbooking.repository.HotelRepository;
import com.hotelbooking.repository.RoomRepository;
import com.hotelbooking.repository.UserRepository;
import com.hotelbooking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {
    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;

    @GetMapping("/bookings")
    public List<BookingResponse> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/analytics")
    public DashboardResponse getAnalytics() {
        List<BookingResponse> bookings = bookingService.getAllBookings();
        BigDecimal totalRevenue = bookings.stream()
                .filter(booking -> PaymentStatus.SUCCESS.name().equals(booking.getPaymentStatus()))
                .map(BookingResponse::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long cancelledBookings = bookings.stream()
                .filter(booking -> "CANCELLED".equals(booking.getBookingStatus()))
                .count();

        return DashboardResponse.builder()
                .totalUsers(userRepository.count())
                .totalHotels(hotelRepository.count())
                .totalRooms(roomRepository.count())
                .totalBookings(bookings.size())
                .cancelledBookings(cancelledBookings)
                .totalRevenue(totalRevenue)
                .build();
    }

    @PutMapping("/bookings/{id}/status")
    public ResponseEntity<BookingResponse> updateBookingStatus(@PathVariable Long id,
                                                               @Valid @RequestBody BookingStatusUpdateRequest request) {
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, request.getBookingStatus()));
    }
}
