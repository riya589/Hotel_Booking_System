package com.hotelbooking.repository;

import com.hotelbooking.model.Booking;
import com.hotelbooking.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Booking> findByHotelIdOrderByCreatedAtDesc(Long hotelId);

    List<Booking> findByRoomIdOrderByCreatedAtDesc(Long roomId);

    List<Booking> findAllByOrderByCreatedAtDesc();

    Optional<Booking> findByIdAndUserId(Long bookingId, Long userId);

    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId " +
            "AND b.bookingStatus IN :activeStatuses " +
            "AND b.checkInDate < :checkOutDate " +
            "AND b.checkOutDate > :checkInDate")
    List<Booking> findOverlappingBookings(@Param("roomId") Long roomId,
                                          @Param("checkInDate") LocalDate checkInDate,
                                          @Param("checkOutDate") LocalDate checkOutDate,
                                          @Param("activeStatuses") List<BookingStatus> activeStatuses);
}
