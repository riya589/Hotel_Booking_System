package com.hotelbooking.repository;

import com.hotelbooking.model.Hotel;
import com.hotelbooking.model.Room;
import com.hotelbooking.model.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHotel(Hotel hotel);

    List<Room> findByHotelId(Long hotelId);

    List<Room> findByHotelIdAndAvailableTrue(Long hotelId);

    List<Room> findByHotelIdAndRoomType(Long hotelId, RoomType roomType);

    Optional<Room> findByRoomNumberIgnoreCase(String roomNumber);
}
