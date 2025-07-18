package com.spribe.booking.util.data_initializer;

import org.springframework.stereotype.Component;

@Component
public class DataInitializer {
//    private final UnitRepository unitRepository;
//    private final EventRepository eventRepository;

//    public DataInitializer(UnitRepository unitRepository, EventRepository eventRepository) {
//        this.unitRepository = unitRepository;
//        this.eventRepository = eventRepository;
//    }



//    @EventListener(ApplicationReadyEvent.class)
//    public void generateRandomUnits() {
//        Random rand = new Random();
//        List<Unit> randomUnits = IntStream.range(0, 90)
//            .mapToObj(i -> {
//                Unit u = new Unit();
//                u.setRooms(rand.nextInt(5) + 1);
//                u.setAccommodationType(randomAccommodation());
//                u.setFloor(rand.nextInt(10));
//                u.setBasePrice(BigDecimal.valueOf(50 + rand.nextDouble() * 200));
//                u.setDescription("Random unit " + i);
//                // etc.
//                return u;
//            })
//            .toList();
//        unitRepository.saveAll(randomUnits);
//    }

//    private AccommodationType randomAccommodation() {
//        AccommodationType[] types = AccommodationType.values();
//        return types[new Random().nextInt(types.length)];
//    }
}
