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
public class HotelResponse {
    private Long id;
    private String name;
    private String location;
    private String address;
    private String description;
    private String amenities;
    private String imageUrl;
    private Double rating;
    private Integer totalRooms;
    private Integer availableRooms;
    private BigDecimal lowestPricePerNight;
    private LocalDateTime createdAt;
}
