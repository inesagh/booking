package com.spribe.booking.unit.rest;

import com.spribe.booking.unit.rest.models.PageDto;
import com.spribe.booking.unit.rest.models.UnitRequestDto;
import com.spribe.booking.unit.rest.models.UnitResponseDto;
import com.spribe.booking.unit.rest.models.UnitSearchCriteria;
import com.spribe.booking.unit.service.UnitService;
import com.spribe.booking.infrastructure.util.type.UnitType;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/units")
public class UnitController {
    private final UnitService unitService;

    @Autowired
    public UnitController(UnitService unitService, ApplicationEventPublisher eventPublisher) {
        this.unitService = unitService;
    }

    @PostMapping
    public ResponseEntity<Object> addUnit(@Valid @RequestBody UnitRequestDto requestDto) {
        UnitResponseDto createdUnit = unitService.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUnit);
    }

    @GetMapping
    public ResponseEntity<PageDto<UnitResponseDto>> getUnits(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer roomsCount,
            @RequestParam(required = false) UnitType type,
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) Boolean available,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort
    ) {
        UnitSearchCriteria criteria = new UnitSearchCriteria();
        criteria.setStartDate(startDate);
        criteria.setEndDate(endDate);
        criteria.setMinPrice(minPrice);
        criteria.setMaxPrice(maxPrice);
        criteria.setRoomsCount(roomsCount);
        criteria.setType(type);
        criteria.setFloor(floor);
        criteria.setAvailable(available);
        criteria.setPage(page);
        criteria.setSize(size);
        criteria.setSort(sort);

        return ResponseEntity.ok(unitService.searchUnits(criteria));
    }
}
