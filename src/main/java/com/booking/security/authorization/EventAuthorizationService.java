package com.booking.security.authorization;

import com.booking.entity.Event;
import com.booking.repository.EventRepository;

import org.springframework.stereotype.Service;

@Service
public class EventAuthorizationService {

    private final EventRepository eventRepository;

    public EventAuthorizationService(EventRepository eventRepository) {

        this.eventRepository =eventRepository;
    }

    public boolean isEventOwner(
            Long eventId,
            Long organizerId) {

        Event event =eventRepository.findById(eventId).orElse(null);

        return event != null
                && event.getOrganizer()
                .getId()
                .equals(organizerId);
    }
}
