package com.booking.service.authorization;

import com.booking.entity.Event;

import com.booking.exception.ResourceOwnershipException;

import com.booking.repository.EventRepository;

import org.springframework.stereotype.Service;

@Service
public class EventOwnershipService {

    private final EventRepository eventRepository;

    public EventOwnershipService(
            EventRepository eventRepository) {

        this.eventRepository = eventRepository;
    }

    public void validateEventOwnership(
            Long eventId,
            Long organizerId) {

        Event event =
                eventRepository.findById(eventId)
                        .orElseThrow(() ->
                                new ResourceOwnershipException(
                                        "Event not found"
                                )
                        );

        if (event.getOrganizer() == null
                || !event.getOrganizer()
                .getId()
                .equals(organizerId)) {

            throw new ResourceOwnershipException(
                    "You are not owner of this event"
            );
        }
    }
}
