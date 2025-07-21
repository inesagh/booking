package com.spribe.booking.unit.service;

import com.spribe.booking.TestCacheConfig;
import com.spribe.booking.helper.UnitHelper;
import com.spribe.booking.infrastructure.cache.CountAvailableUnitCacheService;
import com.spribe.booking.infrastructure.util.data_initializer.DataInitializer;
import com.spribe.booking.infrastructure.util.exception.AppException;
import com.spribe.booking.infrastructure.util.type.UnitType;
import com.spribe.booking.unit.rest.models.PageDto;
import com.spribe.booking.unit.rest.models.UnitRequestDto;
import com.spribe.booking.unit.rest.models.UnitResponseDto;
import com.spribe.booking.unit.rest.models.UnitSearchCriteria;
import com.spribe.booking.user.domain.User;
import jakarta.transaction.Transactional;
import org.hibernate.annotations.processing.SQL;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ComponentScan(basePackages = "com.spribe.booking")
@ActiveProfiles("test")
@Import(TestCacheConfig.class)
public class UnitServiceImplTest {
    @Autowired
    private UnitService unitService;

    @Autowired
    private UnitHelper unitHelper;

    @MockBean
    private CountAvailableUnitCacheService cacheService;

    @MockBean
    private ApplicationEventPublisher eventPublisher;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(2L);
    }

    @AfterEach
    void tearDown() {
        unitHelper.cleanAll();
    }

    @Test
    @SQL(value = "delete * from Units")
    void getAvailableCount() {
        unitHelper.createUnit();
        unitHelper.createUnit();

        int count = unitService.getAvailableUnitCount();

        assertThat(count).isEqualTo(2);
    }

    @Test
    void createNewUnitSuccessfully() {
        UnitRequestDto requestDto = new UnitRequestDto();
        requestDto.setUserId(testUser.getId());
        requestDto.setRoomsCount(2);
        requestDto.setPrice(100.0);
        requestDto.setFloor(1);
        requestDto.setType(UnitType.HOME);
        requestDto.setDescription("Nice unit");

        UnitResponseDto responseDto = unitService.create(requestDto);

        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getUserId()).isEqualTo(testUser.getId());
        assertThat(responseDto.getPrice()).isEqualTo(Math.round((requestDto.getPrice() * 1.15 * 100) / 100));
    }

    @Test
    void createNewUnitThrowsException() {
        UnitRequestDto invalidRequest = new UnitRequestDto();
        invalidRequest.setUserId(null);

        assertThatThrownBy(() -> unitService.create(invalidRequest))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Unit cannot be saved");
    }

    @Test
    @Transactional
    void searchAndReturnUnits() {
        unitHelper.createUnit();
        unitHelper.createUnit();

        UnitSearchCriteria criteria = new UnitSearchCriteria();
        criteria.setPage(0);
        criteria.setSize(10);
        criteria.setSort("id,asc");

        PageDto<UnitResponseDto> result = unitService.searchUnits(criteria);

        assertThat(result.content()).isNotNull();
        assertThat(result.content().size()).isEqualTo(2);
    }

    @Test
    void searchShouldReturnEmptyWhenNoResults() {
        UnitSearchCriteria criteria = new UnitSearchCriteria();
        criteria.setPage(0);
        criteria.setSize(10);
        criteria.setSort("id,asc");

        PageDto<UnitResponseDto> result = unitService.searchUnits(criteria);

        assertThat(result).isNotNull();
        assertThat(result.content()).isEmpty();
    }
}
