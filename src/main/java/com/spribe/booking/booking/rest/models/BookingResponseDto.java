package com.spribe.booking.booking.rest.models;

import com.spribe.booking.infrastructure.util.type.BookingStatusType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDto {
    private Long id;
    private Long userId;
    private Long unitId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BookingStatusType status;
}
