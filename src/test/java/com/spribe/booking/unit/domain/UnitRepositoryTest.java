package com.spribe.booking.unit.domain;

import com.spribe.booking.TestCacheConfig;
import com.spribe.booking.helper.UnitHelper;
import com.spribe.booking.user.domain.User;
import com.spribe.booking.infrastructure.util.type.UnitType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({UnitHelper.class, TestCacheConfig.class})
@ActiveProfiles("test")
class UnitRepositoryTest {
    @Autowired
    UnitRepository repository;

    @Autowired
    private UnitHelper unitHelper;

    private Unit unitAvailable;
    private Unit unitUnavailable;

    @BeforeEach
    void setUp() {
        unitAvailable = unitHelper.createUnit(); // available = true by default

        unitUnavailable = unitHelper.createUnit();
        unitUnavailable.setAvailable(false);
        repository.save(unitUnavailable);
    }

    @AfterEach
    void tearDown() {
        unitHelper.cleanAll();
    }

    @Test
    void countOnlyAvailableUnits() {
        int count = repository.countAllByAvailableIsTrue();

        assertThat(count).isEqualTo(1);
    }

    @Test
    void countOnlyAvailableUnitsReturnZeroWhenNoAvailableUnits() {
        unitAvailable.setAvailable(false);
        repository.save(unitAvailable);

        int count = repository.countAllByAvailableIsTrue();

        assertThat(count).isZero();
    }

    @Test
    void countOnlyAvailableUnitsReturnsCountWhenMultipleAvailableUnits() {
        unitHelper.createUnit();
        unitHelper.createUnit();
        int count = repository.countAllByAvailableIsTrue();

        assertThat(count).isEqualTo(3);
    }
}
