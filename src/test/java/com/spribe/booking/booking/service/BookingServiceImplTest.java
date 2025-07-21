package com.spribe.booking.booking.service;

import com.spribe.booking.TestCacheConfig;
import com.spribe.booking.availability.service.AvailabilityService;
import com.spribe.booking.booking.domain.Booking;
import com.spribe.booking.booking.domain.BookingRepository;
import com.spribe.booking.booking.rest.models.BookingRequestDto;
import com.spribe.booking.booking.rest.models.BookingResponseDto;
import com.spribe.booking.helper.UnitHelper;
import com.spribe.booking.infrastructure.cache.CountAvailableUnitCacheService;
import com.spribe.booking.infrastructure.util.exception.AppException;
import com.spribe.booking.infrastructure.util.mapper.AppMapper;
import com.spribe.booking.infrastructure.util.type.BookingStatusType;
import com.spribe.booking.infrastructure.util.type.PaymentStatusType;
import com.spribe.booking.payment.domain.Payment;
import com.spribe.booking.payment.domain.PaymentRepository;
import com.spribe.booking.unit.domain.Unit;
import com.spribe.booking.unit.domain.UnitRepository;
import com.spribe.booking.user.domain.User;
import org.h2.message.DbException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ComponentScan(basePackages = "com.spribe.booking")
@ActiveProfiles("test")
@Import(TestCacheConfig.class)
@Disabled
public class BookingServiceImplTest {
    @Autowired
    private BookingServiceImpl service;

    @Autowired
    private UnitHelper unitHelper;

    @MockBean
    private AvailabilityService availabilityService;

    @MockBean
    private UnitRepository unitRepository;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private PaymentRepository paymentRepository;

    @MockBean
    private AppMapper mapper;

    @MockBean
    private ApplicationEventPublisher eventPublisher;

    @MockBean
    private CountAvailableUnitCacheService cacheService;

    private Unit unit;
    private Booking booking;
    private BookingRequestDto request;

    @BeforeEach
    void setUp() {
        unit = unitHelper.createUnit();
        booking = new Booking();
        request.setUnitId(unit.getId());
        request.setUserId(unit.getUser().getId());
        request.setStartDate(LocalDate.of(2025, 8, 1));
        request.setEndDate(LocalDate.of(2025, 8, 3));

        request = new BookingRequestDto();
        request.setUnitId(unit.getId());
        request.setUserId(unit.getUser().getId());
        request.setStartDate(booking.getStartDate());
        request.setEndDate(booking.getEndDate());
    }

    @AfterEach
    void tearDown() {
        unitHelper.cleanAll();
    }

    @Test
    void bookUnitSuccessfully() {
        when(unitRepository.findById(unit.getId())).thenReturn(Optional.of(unit));
        when(mapper.toEntity(request)).thenReturn(booking);
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingResponseDto responseDto = new BookingResponseDto();

        BookingResponseDto result = service.book(request);

        assertThat(result).isNotNull();
        verify(availabilityService).checkAvailabilityDates(unit.getId(), request.getStartDate(), request.getEndDate());
        verify(availabilityService).removeAvailability(unit.getId(), request.getStartDate(), request.getEndDate());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void bookUnitThrowsExceptionWhenUnitNotFound() {
        when(unitRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.book(request))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Unit doesn't exist");
    }

    @Test
    void cancelBookingSuccessfully() {
        booking.setStatus(BookingStatusType.CONFIRMED);

        when(bookingRepository.findByIdWithUnit(booking.getId())).thenReturn(Optional.of(booking));

        Payment payment = new Payment();
        payment.setAmount(100.0);
        when(paymentRepository.findByBookingId(booking.getId())).thenReturn(payment);

        String message = service.cancel(booking.getId(), booking.getUser().getId());

        assertThat(message).contains("has been cancelled");
        assertThat(payment.isRefunded()).isTrue();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatusType.REFUNDED);
        assertThat(booking.getStatus()).isEqualTo(BookingStatusType.CANCELLED);

        verify(availabilityService).addRangeAndMerge(unit, booking.getStartDate(), booking.getEndDate());
    }

    @Test
    void cancelThrowExceptionWhenBookingNotFound() {
        when(bookingRepository.findByIdWithUnit(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cancel(1L, 1L))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Booking not found");
    }

    @Test
    void cancelThrowExceptionWhenBookingNotYours() {
        User user = new User();
        user.setId(3L);
        booking.setUser(user);
        when(bookingRepository.findByIdWithUnit(booking.getId())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> service.cancel(booking.getId(), 999L))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("not your booking");
    }

    @Test
    void cancelThrowExceptionWhenBookingAlreadyCancelled() {
        booking.setStatus(BookingStatusType.CANCELLED);
        when(bookingRepository.findByIdWithUnit(booking.getId())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> service.cancel(booking.getId(), booking.getUser().getId()))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("already cancelled");
    }
}
