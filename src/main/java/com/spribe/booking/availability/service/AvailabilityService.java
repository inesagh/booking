package com.spribe.booking.availability.service;

import com.spribe.booking.availability.domain.Availability;
import com.spribe.booking.unit.domain.Unit;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityService {
    boolean checkAvailabilityDates(Long unitId, LocalDate from, LocalDate to);
    void removeAvailability(Long unitId, LocalDate bookedFrom, LocalDate bookedTo);
    void addRangeAndMerge(Unit unit, LocalDate from, LocalDate to);
    List<Availability> findAllByUnitId(Long unitId);
}
