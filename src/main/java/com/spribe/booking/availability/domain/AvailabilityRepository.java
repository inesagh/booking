package com.spribe.booking.availability.domain;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByUnitIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long unitId, LocalDate from, LocalDate to);

    List<Availability> findAllByUnitIdOrderByStartDate(Long unitId);

    void deleteAllByUnitId(Long unitId);

    List<Availability> findAllByUnitId(Long unitId);
}
