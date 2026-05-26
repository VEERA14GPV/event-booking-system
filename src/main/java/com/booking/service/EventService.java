package com.booking.service;

import com.booking.entity.Event;
import com.booking.enums.EventType;
import com.booking.repository.EventRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(
            EventRepository eventRepository) {

        this.eventRepository = eventRepository;
    }

    public Event createEvent(Event event) {

        return eventRepository.save(event);
    }

    public Page<Event> getAllEvents(
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(page, size);

        return eventRepository.findAll(pageable);
    }

    public Page<Event> getEventsByType(
            EventType type,
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(page, size);

        return eventRepository.findByType(
                type,
                pageable
        );
    }
}