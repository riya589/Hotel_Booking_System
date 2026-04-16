package com.hotelbooking.util;

import com.hotelbooking.dto.BillResponse;
import com.hotelbooking.dto.BookingResponse;
import com.hotelbooking.dto.HotelDetailsResponse;
import com.hotelbooking.dto.HotelResponse;
import com.hotelbooking.dto.RoomResponse;
import com.hotelbooking.model.Bill;
import com.hotelbooking.model.Booking;
import com.hotelbooking.model.Hotel;
import com.hotelbooking.model.Room;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

public final class DtoMapper {
    private DtoMapper() {
    }

    public static HotelResponse toHotelResponse(Hotel hotel, List<RoomResponse> roomResponses) {
        BigDecimal lowestPrice = roomResponses.stream()
                .map(RoomResponse::getPricePerNight)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        int totalRooms = hotel.getTotalRooms() != null ? hotel.getTotalRooms() : 0;
        int availableRooms = hotel.getAvailableRooms() != null ? hotel.getAvailableRooms() : 0;

        return HotelResponse.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .location(hotel.getLocation())
                .address(hotel.getAddress())
                .description(hotel.getDescription())
                .amenities(hotel.getAmenities())
                .imageUrl(hotel.getImageUrl())
                .rating(hotel.getRating())
                .totalRooms(totalRooms)
                .availableRooms(availableRooms)
                .lowestPricePerNight(lowestPrice)
                .createdAt(hotel.getCreatedAt())
                .build();
    }

    public static HotelDetailsResponse toHotelDetailsResponse(Hotel hotel, List<RoomResponse> roomResponses) {
        HotelResponse summary = toHotelResponse(hotel, roomResponses);
        return HotelDetailsResponse.builder()
                .id(summary.getId())
                .name(summary.getName())
                .location(summary.getLocation())
                .address(summary.getAddress())
                .description(summary.getDescription())
                .amenities(summary.getAmenities())
                .imageUrl(summary.getImageUrl())
                .roomImageUrl(hotel.getRoomImageUrl())
                .washroomImageUrl(hotel.getWashroomImageUrl())
                .balconyImageUrl(hotel.getBalconyImageUrl())
                .rating(summary.getRating())
                .lowestPricePerNight(summary.getLowestPricePerNight())
                .totalRooms(summary.getTotalRooms())
                .availableRooms(summary.getAvailableRooms())
                .createdAt(summary.getCreatedAt())
                .rooms(roomResponses)
                .build();
    }

    public static RoomResponse toRoomResponse(Room room, boolean availableForDates) {
        return RoomResponse.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .roomType(room.getRoomType().name())
                .pricePerNight(room.getPricePerNight())
                .capacity(room.getCapacity())
                .enabled(room.getAvailable())
                .availableForDates(availableForDates)
                .hotelId(room.getHotel().getId())
                .hotelName(room.getHotel().getName())
                .createdAt(room.getCreatedAt())
                .build();
    }

    public static BookingResponse toBookingResponse(Booking booking) {
        return BookingResponse.builder()
                .bookingId(booking.getId())
                .hotelId(booking.getHotel().getId())
                .hotelName(booking.getHotel().getName())
                .roomId(booking.getRoom().getId())
                .roomNumber(booking.getRoom().getRoomNumber())
                .roomType(booking.getRoom().getRoomType().name())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .numberOfGuests(booking.getNumberOfGuests())
                .totalNights(booking.getTotalNights())
                .pricePerNight(booking.getRoom().getPricePerNight())
                .totalPrice(booking.getTotalPrice())
                .bookingStatus(booking.getBookingStatus().name())
                .paymentStatus(booking.getPaymentStatus().name())
                .createdAt(booking.getCreatedAt())
                .build();
    }

    public static BillResponse toBillResponse(Bill bill) {
        Booking booking = bill.getBooking();
        return BillResponse.builder()
                .billId(bill.getId())
                .bookingId(booking.getId())
                .hotelName(booking.getHotel().getName())
                .roomNumber(booking.getRoom().getRoomNumber())
                .roomType(booking.getRoom().getRoomType().name())
                .checkInDate(booking.getCheckInDate().toString())
                .checkOutDate(booking.getCheckOutDate().toString())
                .totalNights(booking.getTotalNights())
                .baseAmount(bill.getBaseAmount())
                .taxAmount(bill.getTaxAmount())
                .serviceCharge(bill.getServiceCharge())
                .discountAmount(bill.getDiscountAmount())
                .finalAmount(bill.getFinalAmount())
                .generatedAt(bill.getGeneratedAt())
                .build();
    }
}
