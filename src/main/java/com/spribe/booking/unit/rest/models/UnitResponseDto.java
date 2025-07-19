package com.spribe.booking.unit.rest.models;

import com.spribe.booking.availability.domain.Availability;
import com.spribe.booking.availability.rest.model.AvailabilityResponseDto;
import com.spribe.booking.util.type.UnitType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
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
