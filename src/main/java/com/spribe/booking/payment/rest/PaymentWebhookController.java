package com.spribe.booking.payment.rest;

import com.spribe.booking.availability.service.AvailabilityService;
import com.spribe.booking.booking.domain.Booking;
import com.spribe.booking.booking.domain.BookingRepository;
import com.spribe.booking.event.model.AppEvent;
import com.spribe.booking.payment.domain.Payment;
import com.spribe.booking.payment.domain.PaymentRepository;
import com.spribe.booking.util.exception.AppException;
import com.spribe.booking.util.type.BookingStatusType;
import com.spribe.booking.util.type.PaymentStatusType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentWebhookController {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final AvailabilityService availabilityService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public PaymentWebhookController(PaymentRepository paymentRepository, BookingRepository bookingRepository,
            AvailabilityService availabilityService, ApplicationEventPublisher eventPublisher) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.availabilityService = availabilityService;
        this.eventPublisher = eventPublisher;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestParam Long paymentId,
            @RequestParam Long userId,
            @RequestParam String status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new AppException("Payment not found", HttpStatus.NOT_FOUND));

        Booking booking = payment.getBooking();
        if (!booking.getUser().getId().equals(userId)) {
            throw new AppException("User mismatch", HttpStatus.FORBIDDEN);
        }
        if (payment.getStatus() != PaymentStatusType.PENDING) {
            return ResponseEntity.ok(String.format("Already processed: %s", payment.getStatus()));
        }

        if ("SUCCESS".equalsIgnoreCase(status)) {
            payment.setStatus(PaymentStatusType.SUCCESS);
            booking.setStatus(BookingStatusType.CONFIRMED);

            eventPublisher.publishEvent(new AppEvent(
                    "UNIT_BOOKING_AND_PAYMENT_CONFIRMATION",
                    "Unit " + booking.getUnit().getId() + " booking by User " + booking.getUser().getId()
                            + " got confirmed and payment got succeed",
                    LocalDateTime.now()
            ));
        } else {
            payment.setStatus(PaymentStatusType.FAILED);
            booking.setStatus(BookingStatusType.CANCELLED);

            availabilityService.addRangeAndMerge(
                    booking.getUnit(),
                    booking.getStartDate(),
                    booking.getEndDate()
            );

            eventPublisher.publishEvent(new AppEvent(
                    "FAILED_UNIT_BOOKING_AND_PAYMENT",
                    "Unit " + booking.getUnit().getId() + " booking by User " + booking.getUser().getId()
                            + " got cancelled because of failed payment",
                    LocalDateTime.now()
            ));
        }

        paymentRepository.save(payment);
        bookingRepository.save(booking);
        return ResponseEntity.ok("Payment updated");
    }
}
