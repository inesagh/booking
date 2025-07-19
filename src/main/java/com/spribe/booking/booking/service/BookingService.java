package com.spribe.booking.booking.service;

import com.spribe.booking.booking.rest.models.BookingRequestDto;
import com.spribe.booking.booking.rest.models.BookingResponseDto;

public interface BookingService {
    BookingResponseDto book(BookingRequestDto requestDto);
    String cancel(Long bookingId, Long userId);
}
