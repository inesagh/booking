package com.spribe.booking.unit.rest.models;

import com.spribe.booking.infrastructure.util.type.UnitType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UnitSearchCriteria {
    private LocalDate startDate;
    private LocalDate endDate;
    private Double minPrice;
    private Double maxPrice;
    private Integer roomsCount;
    private UnitType type;
    private Integer floor;
    private Boolean available;

    private int page = 0;
    private int size = 10;
    private String sort = "id,asc";
}
