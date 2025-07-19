package com.spribe.booking.unit.service;

import com.spribe.booking.availability.domain.AvailabilityRepository;
import com.spribe.booking.event.model.AppEvent;
import com.spribe.booking.unit.domain.Unit;
import com.spribe.booking.unit.domain.UnitRepository;
import com.spribe.booking.unit.rest.models.PageDto;
import com.spribe.booking.unit.rest.models.UnitRequestDto;
import com.spribe.booking.unit.rest.models.UnitResponseDto;
import com.spribe.booking.unit.rest.models.UnitSearchCriteria;
import com.spribe.booking.util.exception.AppException;
import com.spribe.booking.util.mapper.AppMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class UnitServiceImpl implements UnitService{
    private final UnitRepository repository;
    private final AppMapper mapper;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public UnitServiceImpl(UnitRepository repository, AvailabilityRepository availabilityRepository, AppMapper mapper,
            ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.mapper = mapper;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public PageDto<UnitResponseDto> searchUnits(UnitSearchCriteria criteria) {
        String[] sortParams = criteria.getSort().split(",");
        Sort sort = Sort.by(Sort.Direction.fromString(sortParams[1]), sortParams[0]);

        PageRequest pageRequest = PageRequest.of(criteria.getPage(), criteria.getSize(), sort);
        Specification<Unit> spec = UnitSpecification.withCriteria(criteria);

        Page<Unit> units = repository.findAll(spec, pageRequest);
        List<UnitResponseDto> content = units.getContent().stream()
                .map(mapper::toResponseDto)
                .toList();

        PageDto<UnitResponseDto> response = new PageDto<>(
            content,
            units.getNumber(),
            units.getSize(),
            units.getTotalElements(),
            units.getTotalPages()
        );

        eventPublisher.publishEvent(new AppEvent(
                "UNITS_FETCHED",
                "All units are fetched",
                LocalDateTime.now()
        ));

        return response;
    }

    @Override
    public UnitResponseDto create(UnitRequestDto requestDto) {
        try{
            Unit newUnitToBeSaved = mapper.toEntity(requestDto);
            Unit saved = repository.save(newUnitToBeSaved);

            eventPublisher.publishEvent(new AppEvent(
                    "UNIT_CREATED",
                    "Unit " + saved.getId() + " created by User " + saved.getUser().getId(),
                    LocalDateTime.now()
            ));

            return mapper.toResponseDto(saved);
        } catch (Exception exception) {
            eventPublisher.publishEvent(new AppEvent(
                    "FAILED_UNIT_CREATED",
                    "Unit creation by User " + requestDto.getUserId() + " failed",
                    LocalDateTime.now()
            ));
            throw new AppException(String.format("Unit cannot be saved. Reason %s", exception.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
