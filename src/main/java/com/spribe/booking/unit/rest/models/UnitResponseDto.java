package com.spribe.booking.unit.rest.models;

import com.spribe.booking.availability.rest.model.AvailabilityResponseDto;
import com.spribe.booking.infrastructure.util.type.UnitType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitResponseDto {
    private Long id;
    private Integer roomsCount;

    @Enumerated(EnumType.STRING)
    private UnitType type;

    private Integer floor;
    private Double price;
    private String description;
    private Boolean available;
    private Long userId;
    private List<AvailabilityResponseDto> availabilityDates;
}
