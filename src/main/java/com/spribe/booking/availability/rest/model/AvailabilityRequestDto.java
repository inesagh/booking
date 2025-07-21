package com.spribe.booking.availability.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityRequestDto {
    private LocalDate startDate;
    private LocalDate endDate;
}
