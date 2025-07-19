package com.spribe.booking.util.data_initializer;

import com.spribe.booking.availability.domain.Availability;
import com.spribe.booking.cache.CountAvailableUnitCacheService;
import com.spribe.booking.event.domain.Event;
import com.spribe.booking.event.domain.EventRepository;
import com.spribe.booking.unit.domain.Unit;
import com.spribe.booking.unit.domain.UnitRepository;
import com.spribe.booking.user.domain.User;
import com.spribe.booking.user.domain.UserRepository;
import com.spribe.booking.util.exception.AppException;
import com.spribe.booking.util.type.UnitType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DataInitializer {
    private final UnitRepository unitRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CountAvailableUnitCacheService cacheService;

    @Autowired
    public DataInitializer(UnitRepository unitRepository, EventRepository eventRepository,
            UserRepository userRepository, CountAvailableUnitCacheService cacheService) {
        this.unitRepository = unitRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.cacheService = cacheService;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void generateRandomUnits() {
        User user = userRepository.findById(3L)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

//        Insert 90 units
        List<Unit> unitsToSave = new ArrayList<>();

        for (int i = 0; i < 90; i++) {
            Unit u = buildRandomUnit(user, i);
            unitsToSave.add(u);
        }
        List<Unit> savedUnits = unitRepository.saveAll(unitsToSave);
        cacheService.updateCountOfAvailableUnits();

//        Insert 90 events
        List<Event> events = savedUnits.stream()
                .map(u -> new Event(null, "UNIT_CREATED",
                        String.format("Unit %d created by the User %d ", u.getId(), user.getId()),
                        LocalDateTime.now()))
                .toList();
        eventRepository.saveAll(events);
    }

    private Unit buildRandomUnit(User user, int i) {
        double basePrice = randomDouble(50, 500);
        double finalPrice = Math.round(basePrice * 1.15 * 100.0) / 100.0;

        Unit unit = new Unit();
        unit.setRoomsCount(randomInt(1, 5));
        unit.setType(randomAccommodation());
        unit.setFloor(randomInt(1, 10));
        unit.setBasePrice(basePrice);
        unit.setFinalPrice(finalPrice);
        unit.setDescription("Random unit #" + (i + 1));
        unit.setAvailable(true);
        unit.setUser(user);

        Availability availability = new Availability();
        availability.setStartDate(LocalDate.now().plusDays(randomInt(1, 10)));
        availability.setEndDate(availability.getStartDate().plusDays(randomInt(5, 15)));
        availability.setUnit(unit);
        unit.setAvailabilityDates(List.of(availability));

        return unit;
    }

    private UnitType randomAccommodation() {
        UnitType[] types = UnitType.values();
        return types[new Random().nextInt(types.length)];
    }

    private int randomInt(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }

    private double randomDouble(double min, double max) {
        return  (double) Math.round(min + (max - min) * new Random().nextDouble() * 100) / 100;
    }
}
