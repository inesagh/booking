package com.spribe.booking.event.listener;

import com.spribe.booking.event.domain.EventRepository;
import com.spribe.booking.event.model.AppEvent;
import com.spribe.booking.util.mapper.AppMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class AppEventListener {
    private final EventRepository eventRepository;
    private final AppMapper appMapper;

    @Autowired
    public AppEventListener(EventRepository eventRepository, AppMapper appMapper) {
        this.eventRepository = eventRepository;
        this.appMapper = appMapper;
    }

    @TransactionalEventListener
    @Async
    public void handle(AppEvent appEvent) {
        eventRepository.save(appMapper.toEntity(appEvent));
    }
}
