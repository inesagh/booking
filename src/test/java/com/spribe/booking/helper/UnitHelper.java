package com.spribe.booking.helper;

import com.spribe.booking.availability.domain.Availability;
import com.spribe.booking.availability.domain.AvailabilityRepository;
import com.spribe.booking.infrastructure.util.type.UnitType;
import com.spribe.booking.unit.domain.Unit;
import com.spribe.booking.unit.domain.UnitRepository;
import com.spribe.booking.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class UnitHelper {
    @Autowired
    private UnitRepository unitRepository;
    @Autowired
    private AvailabilityRepository availabilityRepository;

    public Unit createUnit() {
        Unit unit = new Unit();
        unit.setRoomsCount(2);
        unit.setType(UnitType.HOME);
        unit.setFloor(1);
        unit.setBasePrice(100.0);
        unit.setFinalPrice(115.0);
        unit.setDescription("Test Unit");
        unit.setAvailable(true);

        User user = new User();
        user.setId(1L);
        unit.setUser(user);
        return unitRepository.save(unit);
    }

    public Availability createAvailability(Unit unit, LocalDate start, LocalDate end) {
        Availability availability = new Availability();
        availability.setUnit(unit);
        availability.setStartDate(start);
        availability.setEndDate(end);
        return availabilityRepository.save(availability);
    }

    public void cleanAll() {
        availabilityRepository.deleteAll();
        unitRepository.deleteAll();
    }
}
