package com.hotelbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelDetailsResponse {
    private Long id;
    private String name;
    private String location;
    private String address;
    private String description;
    private String amenities;
    private String imageUrl;
    private String roomImageUrl;
    private String washroomImageUrl;
    private String balconyImageUrl;
    private Double rating;
    private BigDecimal lowestPricePerNight;
    private Integer totalRooms;
    private Integer availableRooms;
    private LocalDateTime createdAt;
    private List<RoomResponse> rooms;
}
