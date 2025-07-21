package com.spribe.booking.booking.domain;

import com.spribe.booking.TestCacheConfig;
import com.spribe.booking.helper.BookingHelper;
import com.spribe.booking.helper.UnitHelper;
import com.spribe.booking.unit.domain.Unit;
import com.spribe.booking.user.domain.User;
import com.spribe.booking.infrastructure.util.type.BookingStatusType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({ UnitHelper.class, BookingHelper.class, TestCacheConfig.class })
@ActiveProfiles("test")
class BookingRepositoryTest {
    @Autowired
    BookingRepository repository;

    @Autowired
    private BookingHelper bookingHelper;

    private Booking testBooking;

    @BeforeEach
    void setUp() {
        testBooking = bookingHelper.createBooking();
    }

    @AfterEach
    void tearDown() {
        bookingHelper.cleanAll();
    }

    @Test
    void findByIdWithUnitReturnsBookingWithUnit() {
        Optional<Booking> result = repository.findByIdWithUnit(testBooking.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(testBooking.getId());
        assertThat(result.get().getUnit()).isNotNull();
    }

    @Test
    void findByIdWithUnitReturnsEmptyWhenIdNotFound() {
        Optional<Booking> result = repository.findByIdWithUnit(9999L);
        assertThat(result).isEmpty();
    }
}
