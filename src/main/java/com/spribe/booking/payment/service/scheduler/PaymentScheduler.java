package com.spribe.booking.payment.service.scheduler;

import com.spribe.booking.availability.service.AvailabilityService;
import com.spribe.booking.booking.domain.Booking;
import com.spribe.booking.booking.domain.BookingRepository;
import com.spribe.booking.event.model.AppEvent;
import com.spribe.booking.payment.domain.Payment;
import com.spribe.booking.payment.domain.PaymentRepository;
import com.spribe.booking.util.type.BookingStatusType;
import com.spribe.booking.util.type.PaymentStatusType;
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
    private final AvailabilityService availabilityService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public PaymentScheduler(BookingRepository bookingRepository, PaymentRepository paymentRepository,
            AvailabilityService availabilityService, ApplicationEventPublisher eventPublisher) {
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.availabilityService = availabilityService;
        this.eventPublisher = eventPublisher;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkPendingPayments() {
        LocalDateTime expiredTime = LocalDateTime.now().minusMinutes(1); //TODO

        List<Payment> expiredPayments = paymentRepository
                .findAllByStatusAndCreatedAtBefore(PaymentStatusType.PENDING, expiredTime);

        for (Payment payment : expiredPayments) {
            Booking booking = payment.getBooking();

            if (mockPaymentSuccessful(payment) && booking.getStatus().equals(BookingStatusType.PENDING)) {
                //change availability
                payment.setStatus(PaymentStatusType.SUCCESS);
                booking.setStatus(BookingStatusType.CONFIRMED);
                availabilityService.removeAvailability(booking.getUnit().getId(), booking.getStartDate(), booking.getEndDate());

                eventPublisher.publishEvent(new AppEvent(
                        "UNIT_BOOKING_AND_PAYMENT_CONFIRMATION",
                        "Unit " + booking.getUnit().getId() + " booking by User " + booking.getUser().getId()
                                + " got confirmed and payment got succeed",
                        LocalDateTime.now()
                ));

            } else {
                payment.setStatus(PaymentStatusType.FAILED);
                booking.setStatus(BookingStatusType.CANCELLED);

                eventPublisher.publishEvent(new AppEvent(
                        "FAILED_UNIT_BOOKING_AND_PAYMENT",
                        "Unit " + booking.getUnit().getId() + " booking by User " + booking.getUser().getId()
                                + " got cancelled because of failed payment",
                        LocalDateTime.now()
                ));

            }

            paymentRepository.save(payment);
            bookingRepository.save(booking);
        }

    }

    private boolean mockPaymentSuccessful(Payment payment) {
//        now - emulation, update later
        return Math.random() > 0.5;
    }
}
