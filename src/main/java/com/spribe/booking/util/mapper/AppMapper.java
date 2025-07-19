package com.spribe.booking.util.mapper;

import com.spribe.booking.availability.domain.Availability;
import com.spribe.booking.availability.rest.model.AvailabilityRequestDto;
import com.spribe.booking.availability.rest.model.AvailabilityResponseDto;
import com.spribe.booking.booking.domain.Booking;
import com.spribe.booking.booking.rest.models.BookingRequestDto;
import com.spribe.booking.booking.rest.models.BookingResponseDto;
import com.spribe.booking.event.domain.Event;
import com.spribe.booking.event.model.AppEvent;
import com.spribe.booking.unit.domain.Unit;
import com.spribe.booking.unit.rest.models.UnitRequestDto;
import com.spribe.booking.unit.rest.models.UnitResponseDto;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AppMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "finalPrice", target = "price")
    UnitResponseDto toResponseDto(Unit unit);

    @Mapping(source = "price", target = "basePrice")
    @Mapping(source = "userId", target = "user.id")
    @Mapping(target = "finalPrice", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "availabilityDates", ignore = true)
    @Mapping(target = "available", ignore = true)
    Unit toEntity(UnitRequestDto requestDto);

    AvailabilityResponseDto toResponseDto(Availability availability);
    List<AvailabilityResponseDto> toResponseDtoList(List<Availability> availability);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "unit", ignore = true)
    Availability toEntity(AvailabilityRequestDto requestDto);

    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "unitId", target = "unit.id")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "id", ignore = true)
    Booking toEntity(BookingRequestDto requestDto);

    @Mapping(source = "unit.id", target = "unitId")
    @Mapping(source = "user.id", target = "userId")
    BookingResponseDto toResponseDto(Booking booking);

    @Mapping(target = "id", ignore = true)
    Event toEntity(AppEvent event);

    @AfterMapping
    default void setFinalPriceAndAvailabilityDetails(@MappingTarget Unit unit, UnitRequestDto requestDto) {
        if(unit.getBasePrice() != null) {
            unit.setFinalPrice((double) Math.round(unit.getBasePrice() * 1.15));
        }

        if (requestDto.getAvailabilityDates() != null) {
            Availability availability = toEntity(requestDto.getAvailabilityDates());
            availability.setUnit(unit);
            unit.setAvailabilityDates(List.of(availability));
            unit.setAvailable(true);
        }
    }
}
