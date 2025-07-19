package com.spribe.booking.booking.rest.models;

import com.spribe.booking.util.type.BookingStatusType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
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
