package com.spribe.booking.availability.domain;

import com.spribe.booking.TestCacheConfig;
import com.spribe.booking.helper.UnitHelper;
import com.spribe.booking.unit.domain.Unit;
import com.spribe.booking.user.domain.User;
import com.spribe.booking.infrastructure.util.type.UnitType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
@Import({UnitHelper.class, TestCacheConfig.class})
@ActiveProfiles("test")
class AvailabilityRepositoryTest {
    @Autowired
    private AvailabilityRepository repository;

    @Autowired
    private UnitHelper unitHelper;

    private Unit testUnit;

    @BeforeEach
    void setUp() {
        testUnit = unitHelper.createUnit();
        unitHelper.createAvailability(testUnit, LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 10));
        unitHelper.createAvailability(testUnit, LocalDate.of(2025, 7, 15), LocalDate.of(2025, 7, 20));
    }

    @AfterEach
    void tearDown() {
        unitHelper.cleanAll();
    }

    @Test void findByUnitIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualReturnsList() {
        List<Availability> result = repository.findByUnitIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                testUnit.getId(), LocalDate.of(2025, 7, 2), LocalDate.of(2025, 7, 5)
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStartDate()).isEqualTo(LocalDate.of(2025, 7, 1));
    }

    @Test void findByUnitIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualReturnsEmptyTest() {
        List<Availability> result = repository
                .findByUnitIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        testUnit.getId(), LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 5));

        assertThat(result).isEmpty();
    }


    @Test
    void findAllByUnitIdOrderByStartDateReturnsList() {
        List<Availability> all = repository.findAllByUnitIdOrderByStartDate(testUnit.getId());

        assertThat(all).hasSize(2);
        assertThat(all).extracting(availability -> availability.getUnit().getId())
                .contains(testUnit.getId());
        assertThat(all).extracting(Availability::getStartDate)
                .isSorted();
    }

    @Test
    void deleteAllByUnitIdRemovesUnit() {
        repository.deleteAllByUnitId(testUnit.getId());

        List<Availability> result = repository.findAllByUnitId(testUnit.getId());
        assertThat(result).isEmpty();
    }

    @Test
    void findAllByUnitIdReturnsList() {
        List<Availability> result = repository.findAllByUnitId(testUnit.getId());

        assertThat(result).hasSize(2);
        assertThat(result).allSatisfy(availability ->
                assertThat(availability.getUnit().getId()).isEqualTo(testUnit.getId()));
    }

    @Test
    void findAllByInvalidUnitIdReturnsEmpty() {
        List<Availability> result = repository.findAllByUnitId(9999L);

        assertThat(result).isEmpty();
    }
}
