package com.spribe.booking.booking.domain;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface BookingRepository extends JpaRepository<Booking, Long> {
}
