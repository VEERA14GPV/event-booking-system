package com.booking.service;

import com.booking.dto.request.EventCreateRequest;

import com.booking.dto.request.EventUpdateRequest;

import com.booking.dto.response.EventResponse;

import com.booking.entity.Event;

import com.booking.enums.EventType;

import com.booking.exception.ResourceNotFoundException;

import com.booking.repository.EventRepository;

import com.booking.specification.EventSpecification;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {

    private final EventRepository eventRepository;

    /*
     * Create event
     */
    public EventResponse createEvent(
            EventCreateRequest request) {

        Event event = new Event();

        event.setName(
                request.getName()
        );

        event.setDescription(
                request.getDescription()
        );

        event.setCity(
                request.getCity()
        );

        event.setLanguage(
                request.getLanguage()
        );

        event.setType(
                request.getType()
        );

        event.setRating(
                request.getRating()
        );

        event.setPrice(
                request.getPrice()
        );

        event.setCreatedAt(
                LocalDateTime.now()
        );

        Event savedEvent =
                eventRepository.save(event);

        return mapToResponse(savedEvent);
    }

    /*
     * Get all events
     */
    @Transactional(readOnly = true)
    public Page<EventResponse> getAllEvents(
            Pageable pageable,
            String city,
            String language,
            EventType type,
            Double rating,
            Double price) {

        Specification<Event> specification =
                Specification.allOf(

                        EventSpecification.hasCity(city),

                        EventSpecification.hasLanguage(language),

                        EventSpecification.hasType(type),

                        EventSpecification.hasMinimumRating(rating),

                        EventSpecification.hasMaximumPrice(price)
                );

        return eventRepository
                .findAll(
                        specification,
                        pageable
                )
                .map(this::mapToResponse);
    }

    /*
     * Get event by ID
     */
    @Transactional(readOnly = true)
    public EventResponse getEventById(
            Long eventId) {

        Event event = eventRepository
                .findById(eventId)
                .orElseThrow(() ->

                        new ResourceNotFoundException(
                                "Event not found with ID: "
                                        + eventId
                        )
                );

        return mapToResponse(event);
    }

    /*
     * Update event
     */
    public EventResponse updateEvent(
            Long eventId,
            EventUpdateRequest request) {

        Event event = eventRepository
                .findById(eventId)
                .orElseThrow(() ->

                        new ResourceNotFoundException(
                                "Event not found with ID: "
                                        + eventId
                        )
                );

        event.setName(
                request.getName()
        );

        event.setDescription(
                request.getDescription()
        );

        event.setCity(
                request.getCity()
        );

        event.setLanguage(
                request.getLanguage()
        );

        event.setType(
        	    EventType.valueOf(
        	        request.getType().toUpperCase()
        	    )
        	);

        event.setRating(
                request.getRating()
        );

        event.setPrice(
                request.getPrice()
        );

        Event updatedEvent =
                eventRepository.save(event);

        return mapToResponse(updatedEvent);
    }

    /*
     * Delete event
     */
    public void deleteEvent(
            Long eventId) {

        Event event = eventRepository
                .findById(eventId)
                .orElseThrow(() ->

                        new ResourceNotFoundException(
                                "Event not found with ID: "
                                        + eventId
                        )
                );

        eventRepository.delete(event);
    }

    /*
     * Entity -> DTO mapper
     */
    private EventResponse mapToResponse(
            Event event) {

        EventResponse response =
                new EventResponse();

        response.setEventId(
                event.getId()
        );

        response.setName(
                event.getName()
        );

        response.setDescription(
                event.getDescription()
        );

        response.setCity(
                event.getCity()
        );

        response.setLanguage(
                event.getLanguage()
        );

        response.setType(
                event.getType()
        );

        response.setRating(
                event.getRating()
        );

        response.setPrice(
                event.getPrice()
        );

        response.setCreatedAt(
                event.getCreatedAt()
        );

        return response;
    }
}