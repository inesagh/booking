package com.spribe.booking.availability.service;

import com.spribe.booking.unit.domain.Unit;

import java.time.LocalDate;

public interface AvailabilityService {
    boolean checkAvailabilityDates(Long unitId, LocalDate from, LocalDate to);
    void removeAvailability(Long unitId, LocalDate bookedFrom, LocalDate bookedTo);
    void addRangeAndMerge(Unit unit, LocalDate from, LocalDate to);
}
