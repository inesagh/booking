package com.spribe.booking.unit.service;

import com.spribe.booking.unit.rest.models.PageDto;
import com.spribe.booking.unit.rest.models.UnitRequestDto;
import com.spribe.booking.unit.rest.models.UnitResponseDto;
import com.spribe.booking.unit.rest.models.UnitSearchCriteria;

public interface UnitService {
    PageDto<UnitResponseDto> searchUnits(UnitSearchCriteria criteria);
    UnitResponseDto create(UnitRequestDto requestDto);
}
