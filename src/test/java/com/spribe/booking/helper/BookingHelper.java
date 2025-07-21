package com.spribe.booking.helper;

import com.spribe.booking.booking.domain.Booking;
import com.spribe.booking.booking.domain.BookingRepository;
import com.spribe.booking.infrastructure.util.type.BookingStatusType;
import com.spribe.booking.unit.domain.Unit;
import com.spribe.booking.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class BookingHelper {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UnitHelper unitHelper;

    public Booking createBooking() {
        User user = new User();
        user.setId(1L);
        Unit unit = unitHelper.createUnit();

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setUnit(unit);
        booking.setStartDate(LocalDate.of(2025, 7, 2));
        booking.setEndDate(LocalDate.of(2025, 7, 5));
        booking.setStatus(BookingStatusType.CONFIRMED);
        booking.setCreatedAt(LocalDateTime.now());

        return bookingRepository.save(booking);
    }

    public void cleanAll() {
        bookingRepository.deleteAll();
    }
}
