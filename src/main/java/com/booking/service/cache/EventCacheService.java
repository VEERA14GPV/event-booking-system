package com.booking.service.cache;

import com.booking.entity.Event;

import com.booking.repository.EventRepository;

import org.springframework.cache.annotation.Cacheable;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventCacheService {

    private final EventRepository eventRepository;

    public EventCacheService(
            EventRepository eventRepository) {

        this.eventRepository = eventRepository;
    }

    @Cacheable(value = "events")
    public List<Event> getAllEvents() {

        return eventRepository.findAll();
    }

    @Cacheable(
            value = "events",
            key = "#eventId"
    )
    public Event getEventById(Long eventId) {

        return eventRepository.findById(eventId)
                .orElse(null);
    }
}