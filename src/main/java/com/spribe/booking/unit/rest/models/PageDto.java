package com.spribe.booking.unit.rest.models;

import java.util.List;

public record PageDto<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {}
