package com.spribe.booking.event.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AppEvent {
    private String eventType;
    private String description;
    private LocalDateTime occurredAt;
}
