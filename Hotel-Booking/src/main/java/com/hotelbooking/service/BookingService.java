package com.hotelbooking.service;

import com.hotelbooking.dto.BookingRequest;
import com.hotelbooking.dto.BookingResponse;
import com.hotelbooking.exception.BadRequestException;
import com.hotelbooking.exception.ResourceNotFoundException;
import com.hotelbooking.exception.UnauthorizedActionException;
import com.hotelbooking.model.Booking;
import com.hotelbooking.model.BookingStatus;
import com.hotelbooking.model.Hotel;
import com.hotelbooking.model.PaymentStatus;
import com.hotelbooking.model.Room;
import com.hotelbooking.model.User;
import com.hotelbooking.repository.BookingRepository;
import com.hotelbooking.repository.HotelRepository;
import com.hotelbooking.repository.UserRepository;
import com.hotelbooking.util.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final HotelService hotelService;
    private final BillingService billingService;

    @Transactional
    public BookingResponse createBooking(Long userId, BookingRequest request) {
        validateBookingRequest(request);

        Room room = hotelService.getRoomEntity(request.getRoomId());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!Boolean.TRUE.equals(room.getAvailable())) {
            throw new BadRequestException("This room is currently disabled by the admin");
        }

        Hotel hotel = room.getHotel();
        if (hotel.getAvailableRooms() != null && hotel.getAvailableRooms() <= 0) {
            throw new BadRequestException("This hotel is fully booked. No rooms available.");
        }

        if (request.getNumberOfGuests() > room.getCapacity()) {
            throw new BadRequestException("Selected room cannot accommodate the requested number of guests");
        }

        int totalNights = Math.toIntExact(ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate()));
        BigDecimal totalPrice = room.getPricePerNight()
                .multiply(BigDecimal.valueOf(totalNights))
                .setScale(2, RoundingMode.HALF_UP);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setHotel(hotel);
        booking.setRoom(room);
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());
        booking.setNumberOfGuests(request.getNumberOfGuests());
        booking.setTotalNights(totalNights);
        booking.setTotalPrice(totalPrice);
        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setPaymentStatus(PaymentStatus.PENDING);

        // Decrement available rooms
        hotel.setAvailableRooms(Math.max(0, hotel.getAvailableRooms() - 1));
        hotelRepository.save(hotel);

        Booking savedBooking = bookingRepository.save(booking);
        billingService.generateOrUpdateBill(savedBooking);
        return DtoMapper.toBookingResponse(savedBooking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getUserBookings(Long userId) {
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(DtoMapper::toBookingResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(DtoMapper::toBookingResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BookingResponse getBooking(Long bookingId, Long userId, boolean admin) {
        Booking booking = getAuthorizedBooking(bookingId, userId, admin);
        return DtoMapper.toBookingResponse(booking);
    }

    @Transactional
    public BookingResponse cancelBooking(Long bookingId, Long userId, boolean admin) {
        Booking booking = getAuthorizedBooking(bookingId, userId, admin);

        if (booking.getBookingStatus() == BookingStatus.CANCELLED || booking.getBookingStatus() == BookingStatus.CHECKED_OUT) {
            throw new BadRequestException("This booking cannot be cancelled anymore");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        if (booking.getPaymentStatus() == PaymentStatus.SUCCESS) {
            booking.setPaymentStatus(PaymentStatus.REFUNDED);
        }

        // Increment available rooms on cancellation
        Hotel hotel = booking.getHotel();
        if (hotel.getAvailableRooms() != null && hotel.getTotalRooms() != null) {
            hotel.setAvailableRooms(Math.min(hotel.getTotalRooms(), hotel.getAvailableRooms() + 1));
            hotelRepository.save(hotel);
        }

        return DtoMapper.toBookingResponse(bookingRepository.save(booking));
    }

    @Transactional
    public BookingResponse updateBookingStatus(Long bookingId, BookingStatus status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (status == BookingStatus.CONFIRMED && booking.getPaymentStatus() != PaymentStatus.SUCCESS) {
            throw new BadRequestException("Booking can be confirmed only after successful payment");
        }

        booking.setBookingStatus(status);
        return DtoMapper.toBookingResponse(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    public Booking getBookingEntity(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    private Booking getAuthorizedBooking(Long bookingId, Long userId, boolean admin) {
        if (admin) {
            return getBookingEntity(bookingId);
        }
        return bookingRepository.findByIdAndUserId(bookingId, userId)
                .orElseThrow(() -> new UnauthorizedActionException("You are not allowed to access this booking"));
    }

    private void validateBookingRequest(BookingRequest request) {
        LocalDate today = LocalDate.now();
        if (request.getCheckInDate() == null || request.getCheckOutDate() == null) {
            throw new BadRequestException("Check-in and check-out dates are required");
        }
        if (request.getCheckInDate().isBefore(today)) {
            throw new BadRequestException("Check-in date cannot be in the past");
        }
        if (!request.getCheckOutDate().isAfter(request.getCheckInDate())) {
            throw new BadRequestException("Check-out date must be after check-in date");
        }
    }
}
