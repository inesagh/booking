package com.spribe.booking.payment.domain;

import com.spribe.booking.TestCacheConfig;
import com.spribe.booking.booking.domain.Booking;
import com.spribe.booking.unit.domain.Unit;
import com.spribe.booking.user.domain.User;
import com.spribe.booking.infrastructure.util.type.BookingStatusType;
import com.spribe.booking.infrastructure.util.type.PaymentStatusType;
import com.spribe.booking.infrastructure.util.type.UnitType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Disabled
@Import(TestCacheConfig.class)
class PaymentRepositoryTest {
    @Autowired
    PaymentRepository repository;

    @Autowired TestEntityManager entityManager;

    @Test
    void findAllByStatusAndCreatedAtBeforeTest() {
        User user = persistUser();
        Unit unit = persistUnit(user);
        Booking booking = persistBooking(user, unit);

        Payment oldPayment = new Payment();
        oldPayment.setBooking(booking);
        oldPayment.setAmount(100.0);
        oldPayment.setStatus(PaymentStatusType.PENDING);
        oldPayment.setCreatedAt(LocalDateTime.now().minusMinutes(30));
        entityManager.persist(oldPayment);

        Payment newPayment = new Payment();
        newPayment.setBooking(booking);
        newPayment.setAmount(100.0);
        newPayment.setStatus(PaymentStatusType.PENDING);
        newPayment.setCreatedAt(LocalDateTime.now().minusMinutes(5));
        entityManager.persist(newPayment);

        entityManager.flush();

        List<Payment> result = repository.findAllByStatusAndCreatedAtBefore(
                PaymentStatusType.PENDING,
                LocalDateTime.now().minusMinutes(15));

        assertThat(result)
                .extracting(Payment::getId)
                .contains(oldPayment.getId())
                .doesNotContain(newPayment.getId());
    }


    @Test
    void findByBookingIdTest() {
        User user = persistUser();
        Unit unit = persistUnit(user);
        Booking booking = persistBooking(user, unit);

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(200.0);
        payment.setStatus(PaymentStatusType.SUCCESS);
        entityManager.persist(payment);
        entityManager.flush();

        Payment found = repository.findByBookingId(booking.getId());
        assertThat(found).isNotNull();
        assertThat(found.getBooking().getId()).isEqualTo(booking.getId());
        assertThat(found.getStatus()).isEqualTo(PaymentStatusType.SUCCESS);
    }

    @Test
    void findByBookingIdReturnsNullTest() {
        Payment found = repository.findByBookingId(1000L);
        assertThat(found).isNull();
    }


    private Booking persistBooking(User user, Unit unit) {
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setUnit(unit);
        booking.setStartDate(LocalDate.of(2025, 8, 1));
        booking.setEndDate(LocalDate.of(2025, 8, 10));
        booking.setStatus(BookingStatusType.PENDING);
        return entityManager.persist(booking);
    }

    private Unit persistUnit(User owner) {
        Unit unit = new Unit();
        unit.setUser(owner);
        unit.setRoomsCount(1);
        unit.setFloor(1);
        unit.setBasePrice(100.0);
        unit.setFinalPrice(115.0);
        unit.setDescription("Repo‑test unit");
        unit.setAvailable(true);
        unit.setType(UnitType.FLAT);
        return entityManager.persist(unit);
    }

    private User persistUser() {
        User user = new User();
        user.setEmail("repo@test.com");
        user.setFullName("Repo Tester");
        user.setCreatedAt(LocalDateTime.now());
        return entityManager.persist(user);
    }

}
