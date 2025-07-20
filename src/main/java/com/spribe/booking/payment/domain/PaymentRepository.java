package com.spribe.booking.payment.domain;

import com.spribe.booking.util.type.PaymentStatusType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByStatusAndCreatedAtBefore(PaymentStatusType statusType, LocalDateTime time);
    Payment findByBookingId(Long bookingId);
}
