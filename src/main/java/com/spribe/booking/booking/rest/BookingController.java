package com.spribe.booking.booking.rest;

import com.spribe.booking.booking.rest.models.BookingRequestDto;
import com.spribe.booking.booking.rest.models.BookingResponseDto;
import com.spribe.booking.booking.service.BookingService;
import com.spribe.booking.unit.rest.models.UnitRequestDto;
import com.spribe.booking.unit.rest.models.UnitResponseDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping(path = "/book")
    public ResponseEntity<Object> bookUnit(@Valid @RequestBody BookingRequestDto requestDto) {
        BookingResponseDto booked = bookingService.book(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(booked);
    }

    @PutMapping(path = "/cancel")
    public ResponseEntity<Object> cancelBooking(@RequestParam("bookingId") Long bookingId,
            @RequestParam("userId") Long userId) {
        String cancelMessage = bookingService.cancel(bookingId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(cancelMessage);
    }
}
