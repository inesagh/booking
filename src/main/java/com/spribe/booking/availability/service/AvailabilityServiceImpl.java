package com.spribe.booking.availability.service;

import com.spribe.booking.availability.domain.Availability;
import com.spribe.booking.availability.domain.AvailabilityRepository;
import com.spribe.booking.cache.CountAvailableUnitCacheService;
import com.spribe.booking.event.model.AppEvent;
import com.spribe.booking.unit.domain.Unit;
import com.spribe.booking.util.exception.AvailabilityException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class AvailabilityServiceImpl implements AvailabilityService{
    private final AvailabilityRepository repository;
    private final ApplicationEventPublisher eventPublisher;
    private final CountAvailableUnitCacheService cacheService;

    @Autowired
    public AvailabilityServiceImpl(AvailabilityRepository repository, ApplicationEventPublisher eventPublisher,
            CountAvailableUnitCacheService cacheService) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.cacheService = cacheService;
    }

    @Override
    public boolean checkAvailabilityDates(Long unitId, LocalDate from, LocalDate to) {
        boolean isAvailable = !repository.findByUnitIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(unitId, from,
                to).isEmpty();

        eventPublisher.publishEvent(new AppEvent(
                "UNIT_AVAILABILITY_CHECK",
                "Unit " + unitId + " availability date ranges get checked for " + from + " until " + to,
                LocalDateTime.now()
        ));

        if(!isAvailable) {
            throw new AvailabilityException(String.format("Unit %d is not available from %s to %s", unitId, from, to));
        }

        return true;
    }

    @Override
    public void removeAvailability(Long unitId, LocalDate bookedFrom, LocalDate bookedTo) {
        List<Availability> availableDates = repository.findByUnitIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                unitId, bookedFrom, bookedTo);

        for (Availability dateRange: availableDates) {
            if (dateRange.getStartDate().equals(bookedFrom) && dateRange.getEndDate().equals(bookedTo)) {
                // exact match
                repository.delete(dateRange);
                eventPublisher.publishEvent(new AppEvent(
                        "UNIT_AVAILABILITY_UPDATE",
                        "Unit " + unitId + " availability date range booked for " + bookedFrom + " until " + bookedTo,
                        LocalDateTime.now()
                ));

            } else if (dateRange.getStartDate().isBefore(bookedFrom) && dateRange.getEndDate().isAfter(bookedTo)) {
                // booked dates are included in the available range, so the booked dates are removed from the available date list
                Availability left = new Availability(null, dateRange.getUnit(), dateRange.getStartDate(), bookedFrom.minusDays(1));
                Availability right = new Availability(null, dateRange.getUnit(), bookedTo.plusDays(1), dateRange.getEndDate());
                repository.delete(dateRange);
                repository.saveAll(List.of(left, right));

                eventPublisher.publishEvent(new AppEvent(
                        "UNIT_AVAILABILITY_UPDATE",
                        "Unit " + unitId + " availability date range modified for " + bookedFrom + " until " + bookedTo,
                        LocalDateTime.now()
                ));

            } else {
                eventPublisher.publishEvent(new AppEvent(
                        "FAILED_UNIT_AVAILABILITY_UPDATE",
                        "Unit " + unitId + " availability date range modification failed for " + bookedFrom + " until " + bookedTo + " because of overlapping",
                        LocalDateTime.now()
                ));

                throw new AvailabilityException("Booking overlaps partially with existing availability — cannot safely update availability.");
            }
        }
    }

    @Override
    public void addRangeAndMerge(Unit unit, LocalDate from, LocalDate to) {
        List<Availability> ranges = repository.findAllByUnitIdOrderByStartDate(unit.getId());
        ranges.add(new Availability(null, unit, from, to));
        ranges.sort(Comparator.comparing(Availability::getStartDate));

        List<Availability> merged = new ArrayList<>();
        for (Availability eachRange : ranges) {
            if (merged.isEmpty()) {
                merged.add(new Availability(null, unit, eachRange.getStartDate(), eachRange.getEndDate()));
                continue;
            }

            Availability previous = merged.get(merged.size() - 1);
            boolean extendThePreviousEndDateWithTheNextStartDate =
                    !eachRange.getStartDate().isAfter(previous.getEndDate().plusDays(1));

            if (extendThePreviousEndDateWithTheNextStartDate) {
                // extend previous.range to cover eachRange.range
                previous.setEndDate(
                        previous.getEndDate().isAfter(eachRange.getEndDate())
                                ? previous.getEndDate()
                                : eachRange.getEndDate()
                );
            } else {
                merged.add(new Availability(null, unit, eachRange.getStartDate(), eachRange.getEndDate()));
            }
        }

        repository.deleteAllByUnitId(unit.getId());
        repository.saveAll(merged);

        if(!unit.getAvailable()) {
            unit.setAvailable(true);
            cacheService.increment();
        }
    }
}
