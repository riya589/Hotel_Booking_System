package com.hotelbooking.service;

import com.hotelbooking.dto.HotelDetailsResponse;
import com.hotelbooking.dto.HotelRequest;
import com.hotelbooking.dto.HotelResponse;
import com.hotelbooking.dto.RoomRequest;
import com.hotelbooking.dto.RoomResponse;
import com.hotelbooking.exception.BadRequestException;
import com.hotelbooking.exception.ResourceNotFoundException;
import com.hotelbooking.model.BookingStatus;
import com.hotelbooking.model.Hotel;
import com.hotelbooking.model.Room;
import com.hotelbooking.model.RoomType;
import com.hotelbooking.repository.BookingRepository;
import com.hotelbooking.repository.HotelRepository;
import com.hotelbooking.repository.RoomRepository;
import com.hotelbooking.util.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelService {
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    @Transactional(readOnly = true)
    public List<HotelResponse> findHotels(String query,
                                          String location,
                                          RoomType roomType,
                                          BigDecimal minPrice,
                                          BigDecimal maxPrice,
                                          LocalDate checkIn,
                                          LocalDate checkOut,
                                          String sort) {
        List<Hotel> hotels = hotelRepository.findAll();

        return hotels.stream()
                .map(hotel -> DtoMapper.toHotelResponse(hotel, getRoomResponses(hotel, roomType, checkIn, checkOut)))
                .filter(hotel -> matchesQuery(hotel, query))
                .filter(hotel -> matchesLocation(hotel, location))
                .filter(hotel -> matchesPrice(hotel, minPrice, maxPrice))
                .filter(hotel -> roomType == null || hotel.getTotalRooms() > 0)
                .sorted(buildComparator(sort))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HotelDetailsResponse getHotelDetails(Long hotelId, RoomType roomType, LocalDate checkIn, LocalDate checkOut) {
        Hotel hotel = getHotelEntity(hotelId);
        List<RoomResponse> roomResponses = getRoomResponses(hotel, roomType, checkIn, checkOut);
        return DtoMapper.toHotelDetailsResponse(hotel, roomResponses);
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> getAvailableRooms(Long hotelId, RoomType roomType, LocalDate checkIn, LocalDate checkOut) {
        Hotel hotel = getHotelEntity(hotelId);
        return getRoomResponses(hotel, roomType, checkIn, checkOut).stream()
                .filter(RoomResponse::getAvailableForDates)
                .collect(Collectors.toList());
    }

    @Transactional
    public HotelResponse createHotel(HotelRequest request) {
        Hotel hotel = new Hotel();
        applyHotelRequest(hotel, request);
        hotelRepository.save(hotel);
        return DtoMapper.toHotelResponse(hotel, List.of());
    }

    @Transactional
    public HotelResponse updateHotel(Long hotelId, HotelRequest request) {
        Hotel hotel = getHotelEntity(hotelId);
        applyHotelRequest(hotel, request);
        hotelRepository.save(hotel);
        return DtoMapper.toHotelResponse(hotel, getRoomResponses(hotel, null, null, null));
    }

    @Transactional
    public void deleteHotel(Long hotelId) {
        Hotel hotel = getHotelEntity(hotelId);
        hotelRepository.delete(hotel);
    }

    @Transactional
    public RoomResponse addRoom(Long hotelId, RoomRequest request) {
        if (roomRepository.findByRoomNumberIgnoreCase(request.getRoomNumber().trim()).isPresent()) {
            throw new BadRequestException("Room number already exists. Please choose a unique room number.");
        }

        Hotel hotel = getHotelEntity(hotelId);
        Room room = new Room();
        room.setHotel(hotel);
        applyRoomRequest(room, request);
        roomRepository.save(room);
        return DtoMapper.toRoomResponse(room, room.getAvailable());
    }

    @Transactional
    public RoomResponse updateRoom(Long roomId, RoomRequest request) {
        Room room = getRoomEntity(roomId);
        roomRepository.findByRoomNumberIgnoreCase(request.getRoomNumber().trim())
                .filter(existing -> !existing.getId().equals(roomId))
                .ifPresent(existing -> {
                    throw new BadRequestException("Room number already exists. Please choose a unique room number.");
                });

        applyRoomRequest(room, request);
        roomRepository.save(room);
        return DtoMapper.toRoomResponse(room, room.getAvailable());
    }

    @Transactional
    public void deleteRoom(Long roomId) {
        Room room = getRoomEntity(roomId);
        roomRepository.delete(room);
    }

    @Transactional(readOnly = true)
    public Room getRoomEntity(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
    }

    @Transactional(readOnly = true)
    public Hotel getHotelEntity(Long hotelId) {
        return hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
    }

    private void applyHotelRequest(Hotel hotel, HotelRequest request) {
        hotel.setName(request.getName().trim());
        hotel.setLocation(request.getLocation().trim());
        hotel.setAddress(request.getAddress().trim());
        hotel.setDescription(request.getDescription());
        hotel.setAmenities(request.getAmenities());
        hotel.setImageUrl(StringUtils.hasText(request.getImageUrl()) ? request.getImageUrl().trim() : null);
        hotel.setRoomImageUrl(StringUtils.hasText(request.getRoomImageUrl()) ? request.getRoomImageUrl().trim() : null);
        hotel.setWashroomImageUrl(StringUtils.hasText(request.getWashroomImageUrl()) ? request.getWashroomImageUrl().trim() : null);
        hotel.setBalconyImageUrl(StringUtils.hasText(request.getBalconyImageUrl()) ? request.getBalconyImageUrl().trim() : null);
        hotel.setRating(request.getRating() != null ? request.getRating() : 4.5);
        int rooms = request.getTotalRooms() != null && request.getTotalRooms() > 0 ? request.getTotalRooms() : 5;
        hotel.setTotalRooms(rooms);
        // Only reset availableRooms on creation (when id is null)
        if (hotel.getId() == null) {
            hotel.setAvailableRooms(rooms);
        }
    }

    private void applyRoomRequest(Room room, RoomRequest request) {
        room.setRoomNumber(request.getRoomNumber().trim().toUpperCase(Locale.ROOT));
        room.setRoomType(request.getRoomType());
        room.setPricePerNight(request.getPricePerNight());
        room.setCapacity(request.getCapacity());
        room.setAvailable(request.getAvailable() == null || request.getAvailable());
    }

    private List<RoomResponse> getRoomResponses(Hotel hotel, RoomType roomType, LocalDate checkIn, LocalDate checkOut) {
        return roomRepository.findByHotel(hotel).stream()
                .filter(room -> roomType == null || room.getRoomType() == roomType)
                .map(room -> DtoMapper.toRoomResponse(room, isRoomAvailable(room, checkIn, checkOut)))
                .collect(Collectors.toList());
    }

    private boolean isRoomAvailable(Room room, LocalDate checkIn, LocalDate checkOut) {
        if (!Boolean.TRUE.equals(room.getAvailable())) {
            return false;
        }
        // Always allow booking even for same dates as requested
        return true;
    }

    private boolean matchesQuery(HotelResponse hotel, String query) {
        if (!StringUtils.hasText(query)) {
            return true;
        }
        String term = query.trim().toLowerCase(Locale.ROOT);
        return hotel.getName().toLowerCase(Locale.ROOT).contains(term)
                || hotel.getLocation().toLowerCase(Locale.ROOT).contains(term)
                || hotel.getAddress().toLowerCase(Locale.ROOT).contains(term);
    }

    private boolean matchesLocation(HotelResponse hotel, String location) {
        if (!StringUtils.hasText(location)) {
            return true;
        }
        return hotel.getLocation().toLowerCase(Locale.ROOT).contains(location.trim().toLowerCase(Locale.ROOT));
    }

    private boolean matchesPrice(HotelResponse hotel, BigDecimal minPrice, BigDecimal maxPrice) {
        BigDecimal value = hotel.getLowestPricePerNight();
        if (value == null) {
            return false;
        }
        if (minPrice != null && value.compareTo(minPrice) < 0) {
            return false;
        }
        return maxPrice == null || value.compareTo(maxPrice) <= 0;
    }

    private Comparator<HotelResponse> buildComparator(String sort) {
        if (!StringUtils.hasText(sort)) {
            return Comparator.comparing(HotelResponse::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
        }

        return switch (sort) {
            case "priceAsc" -> Comparator.comparing(HotelResponse::getLowestPricePerNight, Comparator.nullsLast(Comparator.naturalOrder()));
            case "priceDesc" -> Comparator.comparing(HotelResponse::getLowestPricePerNight, Comparator.nullsLast(Comparator.reverseOrder()));
            case "rating" -> Comparator.comparing(HotelResponse::getRating, Comparator.nullsLast(Comparator.reverseOrder()));
            case "name" -> Comparator.comparing(HotelResponse::getName, String.CASE_INSENSITIVE_ORDER);
            default -> Comparator.comparing(HotelResponse::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
        };
    }
}
