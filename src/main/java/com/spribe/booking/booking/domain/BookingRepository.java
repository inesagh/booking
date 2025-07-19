package com.spribe.booking.booking.domain;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Transactional
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("""
       SELECT b
         FROM Booking b
         JOIN FETCH b.unit
        WHERE b.id = :id
    """)
    Optional<Booking> findByIdWithUnit(Long id);
}
