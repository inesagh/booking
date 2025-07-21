package com.spribe.booking.booking.service;

import com.spribe.booking.availability.service.AvailabilityService;
import com.spribe.booking.booking.domain.Booking;
import com.spribe.booking.booking.domain.BookingRepository;
import com.spribe.booking.booking.rest.models.BookingRequestDto;
import com.spribe.booking.booking.rest.models.BookingResponseDto;
import com.spribe.booking.infrastructure.cache.CountAvailableUnitCacheService;
import com.spribe.booking.event.model.AppEvent;
import com.spribe.booking.payment.domain.Payment;
import com.spribe.booking.payment.domain.PaymentRepository;
import com.spribe.booking.unit.domain.Unit;
import com.spribe.booking.unit.domain.UnitRepository;
import com.spribe.booking.infrastructure.util.exception.AppException;
import com.spribe.booking.infrastructure.util.mapper.AppMapper;
import com.spribe.booking.infrastructure.util.type.BookingStatusType;
import com.spribe.booking.infrastructure.util.type.PaymentStatusType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BookingServiceImpl implements BookingService{
    private final AvailabilityService availabilityService;
    private final UnitRepository unitRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final AppMapper mapper;
    private final ApplicationEventPublisher eventPublisher;
    private final CountAvailableUnitCacheService cacheService;

    @Autowired
    public BookingServiceImpl(AvailabilityService availabilityService, UnitRepository unitRepository,
            BookingRepository bookingRepository, PaymentRepository paymentRepository, AppMapper mapper,
            ApplicationEventPublisher eventPublisher, CountAvailableUnitCacheService cacheService) {
        this.availabilityService = availabilityService;
        this.unitRepository = unitRepository;
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.mapper = mapper;
        this.eventPublisher = eventPublisher;
        this.cacheService = cacheService;
    }

    @Override
    @Transactional
    public BookingResponseDto book(BookingRequestDto requestDto) {
        Unit unit = unitRepository.findById(requestDto.getUnitId())
                .orElseThrow(() -> new AppException("Unit doesn't exist", HttpStatus.NOT_FOUND));
        availabilityService.checkAvailabilityDates(unit.getId(), requestDto.getStartDate(), requestDto.getEndDate());

        availabilityService.removeAvailability(unit.getId(), requestDto.getStartDate(), requestDto.getEndDate());

        Booking booking = mapper.toEntity(requestDto);
        Booking savedBooking = bookingRepository.save(booking);

        Payment payment = new Payment();
        payment.setBooking(savedBooking);
        payment.setAmount(unit.getFinalPrice());
        payment.setStatus(PaymentStatusType.PENDING);
        payment.setCreatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        eventPublisher.publishEvent(new AppEvent(
                "UNIT_BOOKING_ADD",
                "Unit " + requestDto.getUnitId() + " booked by User " + requestDto.getUserId(),
                LocalDateTime.now()
        ));

        eventPublisher.publishEvent(new AppEvent(
                "UNIT_BOOKING_PENDING_PAYMENT_ADD",
                "Unit " + requestDto.getUnitId() + " booking by User " + requestDto.getUserId() + " payment adding in progress",
                LocalDateTime.now()
        ));

        if (availabilityService.findAllByUnitId(unit.getId()).isEmpty()) {
            booking.getUnit().setAvailable(false);
            cacheService.decrement();
        }

        return mapper.toResponseDto(booking);
    }

    @Transactional
    @Override
    public String cancel(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findByIdWithUnit(bookingId)
                .orElseThrow(() -> new AppException("Booking not found!", HttpStatus.NOT_FOUND));

        if(!booking.getUser().getId().equals(userId)) {
            throw new AppException("It's not your booking: you cannot cancel it!", HttpStatus.BAD_REQUEST);
        }
        if(booking.getStatus().equals(BookingStatusType.CANCELLED)) {
            throw new AppException("The booking is already cancelled!", HttpStatus.BAD_REQUEST);
        }

//        Cancel the booking - 1. change the booking status, 2. the payment status, refund the payment (emulation - TBC),
//        and 3. update the availability date ranges for that unit
        booking.setStatus(BookingStatusType.CANCELLED);

        Payment payment = paymentRepository.findByBookingId(bookingId);
        payment.setRefunded(true);
        payment.setStatus(PaymentStatusType.REFUNDED);

        availabilityService.addRangeAndMerge(
                booking.getUnit(),
                booking.getStartDate(),
                booking.getEndDate()
        );

        paymentRepository.save(payment);
        bookingRepository.save(booking);


        eventPublisher.publishEvent(new AppEvent(
                "BOOKING_CANCELLED",
                "Booking " + bookingId + " cancelled & refunded by user " + userId,
                LocalDateTime.now()
        ));

        return String.format("Your booking with %s id has been cancelled and the payment of $ %s will be refunded you shortly!",
                bookingId, payment.getAmount().toString());
    }

}
