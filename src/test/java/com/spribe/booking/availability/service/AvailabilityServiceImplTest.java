package com.spribe.booking.availability.service;

import com.spribe.booking.TestCacheConfig;
import com.spribe.booking.availability.domain.Availability;
import com.spribe.booking.helper.UnitHelper;
import com.spribe.booking.infrastructure.cache.CountAvailableUnitCacheService;
import com.spribe.booking.infrastructure.util.data_initializer.DataInitializer;
import com.spribe.booking.infrastructure.util.exception.AvailabilityException;
import com.spribe.booking.unit.domain.Unit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ComponentScan(basePackages = "com.spribe.booking")
@ActiveProfiles("test")
@Import(TestCacheConfig.class)
class AvailabilityServiceImplTest {
    @Autowired
    private AvailabilityService service;

    @Autowired
    private UnitHelper unitHelper;

    @MockBean
    private CountAvailableUnitCacheService countAvailableUnitCacheService;

    private Unit testUnit;

    @BeforeEach
    void setUp() {
        testUnit = unitHelper.createUnit();
        unitHelper.createAvailability(testUnit, LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 10));
    }

    @AfterEach
    void tearDown() {
        unitHelper.cleanAll();
    }

    @Test
    void checkAvailabilityDatesReturnsTrueWhenAvailable() {
        boolean result = service.checkAvailabilityDates(testUnit.getId(), LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 10));
        assertThat(result).isTrue();
    }

    @Test
    void checkAvailabilityDatesThrowsExceptionWhenNotAvailable() {
        LocalDate from = LocalDate.of(2025, 6, 1);
        LocalDate to = LocalDate.of(2025, 6, 5);
        assertThatThrownBy(() -> service.checkAvailabilityDates(testUnit.getId(), from, to))
                .isInstanceOf(AvailabilityException.class)
                .hasMessageContaining("is not available");
    }

    @Test
    void removeAvailabilityDeletesExactMatch() {
        service.removeAvailability(testUnit.getId(), LocalDate.of(2025,7,1), LocalDate.of(2025,7,10));
        assertThat(service.findAllByUnitId(testUnit.getId())).isEmpty();
    }

    @Test
    void removeAvailabilitySplitsAvailabilityWhenBookedInside() {
        service.removeAvailability(testUnit.getId(), LocalDate.of(2025,7,3), LocalDate.of(2025,7,7));

        List<Availability> availabilities = service.findAllByUnitId(testUnit.getId());
        assertThat(availabilities).hasSize(2);
        assertThat(availabilities).extracting(Availability::getStartDate).contains(LocalDate.of(2025,7,1), LocalDate.of(2025,7,8));
        assertThat(availabilities).extracting(Availability::getEndDate).contains(LocalDate.of(2025,7,2), LocalDate.of(2025,7,10));
    }

    @Test
    void removeAvailabilityModifiesStartDateWhenBookedOverlapsStart() {
        service.removeAvailability(testUnit.getId(), LocalDate.of(2025,7,1), LocalDate.of(2025,7,5));

        List<Availability> availabilities = service.findAllByUnitId(testUnit.getId());
        assertThat(availabilities).hasSize(1);
        assertThat(availabilities.get(0).getStartDate()).isEqualTo(LocalDate.of(2025,7,6));
    }

    @Test
    void removeAvailabilityModifiesEndDateWhenBookedOverlapsEnd() {
        service.removeAvailability(testUnit.getId(), LocalDate.of(2025,7,5), LocalDate.of(2025,7,10));

        List<Availability> availabilities = service.findAllByUnitId(testUnit.getId());
        assertThat(availabilities).hasSize(1);
        assertThat(availabilities.get(0).getEndDate()).isEqualTo(LocalDate.of(2025,7,4));
    }

    @Test
    void addRangeAndMergeAddsNewRangeWhenNoOverlap() {
        LocalDate newFrom = LocalDate.of(2025,7,15);
        LocalDate newTo = LocalDate.of(2025,7,20);

        service.addRangeAndMerge(testUnit, newFrom, newTo);

        List<Availability> availabilities = service.findAllByUnitId(testUnit.getId());
        assertThat(availabilities).hasSize(2);
    }

    @Test
    void addRangeAndMergeMergesOverlappingRanges() {
        service.addRangeAndMerge(testUnit, LocalDate.of(2025,7,5), LocalDate.of(2025,7,12));

        List<Availability> availabilities = service.findAllByUnitId(testUnit.getId());
        assertThat(availabilities).hasSize(1);
        assertThat(availabilities.get(0).getStartDate()).isEqualTo(LocalDate.of(2025,7,1));
        assertThat(availabilities.get(0).getEndDate()).isEqualTo(LocalDate.of(2025,7,12));
    }

    @Test
    void addRangeAndMergeMergesAdjacentRanges() {
        service.addRangeAndMerge(testUnit, LocalDate.of(2025,7,11), LocalDate.of(2025,7,15));

        List<Availability> availabilities = service.findAllByUnitId(testUnit.getId());
        assertThat(availabilities).hasSize(1);
        assertThat(availabilities.get(0).getEndDate()).isEqualTo(LocalDate.of(2025,7,15));
    }

    @Test
    void addRangeAndMergeIncrementsCacheWhenUnitUnavailable() {
        testUnit.setAvailable(false);

        service.addRangeAndMerge(testUnit, LocalDate.of(2025,7,11), LocalDate.of(2025,7,15));

        verify(countAvailableUnitCacheService, times(1)).increment();
        assertThat(testUnit.getAvailable()).isTrue();
    }


    @Test void findAllByUnitIdReturnsList() {
        List<Availability> all = service.findAllByUnitId(testUnit.getId());
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getStartDate()).isEqualTo(LocalDate.of(2025, 7, 1));
    }

    @Test
    void findAllByUnitIdReturnsEmptyListWhenNoAvailability() {
        unitHelper.cleanAll();
        List<Availability> availabilities = service.findAllByUnitId(testUnit.getId());
        assertThat(availabilities).isEmpty();
    }

}
