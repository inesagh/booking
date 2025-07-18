package com.spribe.booking.unit.availability.domain;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
}
