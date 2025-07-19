package com.spribe.booking.availability.domain;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
//    @Query("""
//        SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END
//          FROM Availability a
//         WHERE a.unit.id = :unitId
//           AND a.startDate <= :from
//           AND a.endDate   >= :to
//    """)
//    boolean checkDates(@Param("unitId") Long unitId,
//            @Param("from") LocalDate from,
//            @Param("to") LocalDate to);

    List<Availability> findByUnitIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long unitId, LocalDate from, LocalDate to);

    List<Availability> findAllByUnitIdOrderByStartDate(Long unitId);

    void deleteAllByUnitId(Long unitId);
}
