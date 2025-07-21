package com.spribe.booking.booking.rest;

import com.spribe.booking.booking.rest.models.BookingRequestDto;
import com.spribe.booking.booking.rest.models.BookingResponseDto;
import com.spribe.booking.booking.service.BookingService;
import com.spribe.booking.infrastructure.cache.CountAvailableUnitCacheService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/booking")
public class BookingController {
    private final BookingService bookingService;
    private final CountAvailableUnitCacheService cacheService;

    @Autowired
    public BookingController(BookingService bookingService,
            CountAvailableUnitCacheService cacheService) {
        this.bookingService = bookingService;
        this.cacheService = cacheService;
    }

    @PostMapping()
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

    @GetMapping("/available")
    public ResponseEntity<Integer> getAvailableUnitCountToBook() {
        return ResponseEntity.ok(cacheService.getAvailableUnitCount());
    }
}
