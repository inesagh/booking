package com.spribe.booking.cache;

import com.spribe.booking.unit.domain.UnitRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CountAvailableUnitCacheService {
    private static final Logger LOG = LoggerFactory.getLogger(CountAvailableUnitCacheService.class);
    private static final String COUNT_AVAILABLE_UNITS_KEY = "unit:available:count";
    private final RedisTemplate<String, Integer> redisTemplate;
    private final UnitRepository unitRepository;

    public CountAvailableUnitCacheService(RedisTemplate<String, Integer> redisTemplate, UnitRepository unitRepository) {
        this.redisTemplate = redisTemplate;
        this.unitRepository = unitRepository;
    }

    @PostConstruct
    public void init() {
        updateCountOfAvailableUnits();
    }

    public int getAvailableUnitCount() {
        try {
            Integer cachedCount = redisTemplate.opsForValue().get(COUNT_AVAILABLE_UNITS_KEY);
            if (cachedCount == null) {
                updateCountOfAvailableUnits();
                cachedCount = redisTemplate.opsForValue().get(COUNT_AVAILABLE_UNITS_KEY);
            }

            return cachedCount != null ? cachedCount : 0;
        } catch (Exception e) {
            LOG.warn("Redis unavailable, falling back to DB", e);
            return unitRepository.countAllByAvailableIsTrue();
        }
    }

    public void updateCountOfAvailableUnits() {
        int count = unitRepository.countAllByAvailableIsTrue();
        redisTemplate.opsForValue().set(COUNT_AVAILABLE_UNITS_KEY, count);
    }

    @Scheduled(fixedRate = 10 * 60 * 1000) // reload every 10 min
    public void refreshUnitAvailabilityCache() {
        updateCountOfAvailableUnits();
    }


    public void increment() {
        redisTemplate.opsForValue().increment(COUNT_AVAILABLE_UNITS_KEY);
    }

    public void decrement() {
        redisTemplate.opsForValue().decrement(COUNT_AVAILABLE_UNITS_KEY);
    }
}
