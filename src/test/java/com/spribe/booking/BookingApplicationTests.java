package com.spribe.booking;

import com.spribe.booking.infrastructure.cache.CountAvailableUnitCacheService;
import com.spribe.booking.infrastructure.cache.RedisConfig;
import com.spribe.booking.infrastructure.util.data_initializer.DataInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@ImportAutoConfiguration(exclude = { DataInitializer.class, LiquibaseAutoConfiguration.class})
@Import(TestCacheConfig.class)
class BookingApplicationTests {
    @Test
    void contextLoads() {
    }
}
