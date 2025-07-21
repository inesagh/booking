package com.spribe.booking;

import com.spribe.booking.infrastructure.cache.CountAvailableUnitCacheService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestCacheConfig {
    @Bean
    public CountAvailableUnitCacheService countAvailableUnitCacheService() {
        return Mockito.mock(CountAvailableUnitCacheService.class);
    }
}
