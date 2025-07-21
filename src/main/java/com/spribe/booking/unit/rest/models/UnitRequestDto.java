package com.spribe.booking.unit.rest.models;

import com.spribe.booking.availability.rest.model.AvailabilityRequestDto;
import com.spribe.booking.infrastructure.util.type.UnitType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitRequestDto {
    private Integer roomsCount;
    @Enumerated(EnumType.STRING)
    private UnitType type;
    private Integer floor;
    private Double price;
    private String description;
    private AvailabilityRequestDto availabilityDates;
    private Long userId;
}
