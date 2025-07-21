package com.spribe.booking.infrastructure.scheduler;

import com.spribe.booking.availability.domain.AvailabilityRepository;
import com.spribe.booking.availability.service.AvailabilityService;
import com.spribe.booking.booking.domain.Booking;
import com.spribe.booking.booking.domain.BookingRepository;
import com.spribe.booking.infrastructure.cache.CountAvailableUnitCacheService;
import com.spribe.booking.event.model.AppEvent;
import com.spribe.booking.payment.domain.Payment;
import com.spribe.booking.payment.domain.PaymentRepository;
import com.spribe.booking.infrastructure.util.type.BookingStatusType;
import com.spribe.booking.infrastructure.util.type.PaymentStatusType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentScheduler {
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final AvailabilityRepository availabilityRepository;
    private final AvailabilityService availabilityService;
    private final ApplicationEventPublisher eventPublisher;
    private final CountAvailableUnitCacheService cacheService;

    @Autowired
    public PaymentScheduler(BookingRepository bookingRepository, PaymentRepository paymentRepository,
            AvailabilityRepository availabilityRepository, AvailabilityService availabilityService, ApplicationEventPublisher eventPublisher,
            CountAvailableUnitCacheService cacheService) {
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.availabilityRepository = availabilityRepository;
        this.availabilityService = availabilityService;
        this.eventPublisher = eventPublisher;
        this.cacheService = cacheService;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkPendingPayments() {
        LocalDateTime expiredTime = LocalDateTime.now().minusMinutes(15);

        List<Payment> expiredPayments = paymentRepository
                .findAllByStatusAndCreatedAtBefore(PaymentStatusType.PENDING, expiredTime);

        for (Payment payment : expiredPayments) {
            Booking booking = payment.getBooking();
            payment.setStatus(PaymentStatusType.FAILED);
            booking.setStatus(BookingStatusType.CANCELLED);
            availabilityService.addRangeAndMerge(
                    booking.getUnit(),
                    booking.getStartDate(),
                    booking.getEndDate()
            );
            cacheService.increment();

            eventPublisher.publishEvent(new AppEvent(
                    "FAILED_UNIT_BOOKING_AND_PAYMENT",
                    "Unit " + booking.getUnit().getId() + " booking by User " + booking.getUser().getId()
                            + " got cancelled because of failed payment",
                    LocalDateTime.now()
            ));

            paymentRepository.save(payment);
            bookingRepository.save(booking);
        }
    }
}
