package com.spribe.booking.event.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class AppEvent {
    private String eventType;
    private String description;
    private LocalDateTime occurredAt;
}
