package com.booking.service;

import com.booking.dto.request.EventCreateRequest;

import com.booking.dto.request.EventUpdateRequest;

import com.booking.dto.response.EventResponse;

import com.booking.entity.Booking;
import com.booking.entity.Event;
import com.booking.entity.Show;
import com.booking.entity.User;

import com.booking.enums.EventType;

import com.booking.exception.ResourceNotFoundException;

import com.booking.repository.BookingRepository;
import com.booking.repository.BookingSeatRepository;
import com.booking.repository.EventRepository;
import com.booking.repository.PaymentRepository;
import com.booking.repository.SeatLockRepository;
import com.booking.repository.SeatRepository;
import com.booking.repository.ShowRepository;
import com.booking.repository.UserRepository;

import com.booking.service.search.EventSearchService;

import com.booking.specification.EventSpecification;

import com.booking.util.SecurityUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {

    private final EventRepository
            eventRepository;

    private final EventSearchService
            eventSearchService;

    private final UserRepository
            userRepository;

    private final SecurityUtil
            securityUtil;

    private final ShowRepository
            showRepository;

    private final BookingRepository
            bookingRepository;

    private final BookingSeatRepository
            bookingSeatRepository;

    private final PaymentRepository
            paymentRepository;

    private final SeatRepository
            seatRepository;

    private final SeatLockRepository
            seatLockRepository;

    /*
     * Create event.
     *
     * Evicts all cached events
     * so stale list is not served.
     */
    @CacheEvict(
            value = "events",
            allEntries = true
    )
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

        event.setVenue(
                request.getVenue()
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

        Long organizerId =
                securityUtil.getCurrentUserId();

        User organizer =
                userRepository
                        .findById(organizerId)

                        .orElseThrow(() ->

                                new ResourceNotFoundException(

                                        "User not found with ID: "
                                                + organizerId
                                )
                        );

        event.setOrganizer(organizer);

        Event savedEvent =
                eventRepository.save(event);

        /*
         * Sync to Elasticsearch.
         *
         * Resilient: retries + circuit breaker inside
         * EventSearchService, falls back to a logged
         * no-op so this MySQL write is never rolled back
         * or blocked by an Elasticsearch outage.
         */
        eventSearchService.indexEvent(savedEvent);

        return mapToResponse(savedEvent);
    }

    /*
     * Get all events.
     *
     * Dynamic filters applied via
     * JPA Specification.
     *
     * Cache disabled intentionally:
     * filter combinations are too
     * varied to cache effectively.
     */
    @Transactional(readOnly = true)
    public Page<EventResponse> getAllEvents(

            Pageable pageable,

            String city,

            String venue,

            String language,

            EventType type,

            Double rating,

            Double price) {

        Specification<Event> specification =

                Specification.allOf(

                        EventSpecification
                                .hasCity(city),

                        EventSpecification
                                .hasVenue(venue),

                        EventSpecification
                                .hasLanguage(language),

                        EventSpecification
                                .hasType(type),

                        EventSpecification
                                .hasMinimumRating(rating),

                        EventSpecification
                                .hasMaximumPrice(price)
                );

        return eventRepository

                .findAll(
                        specification,
                        pageable
                )

                .map(this::mapToResponse);
    }

    /*
     * Get event by ID.
     *
     * Result cached in Redis.
     *
     * Cache key: eventId
     * TTL: defined in application.properties
     */
    @Cacheable(
            value = "events",
            key = "#eventId"
    )
    @Transactional(readOnly = true)
    public EventResponse getEventById(
            Long eventId) {
        Event event =

                eventRepository
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
     * Update event.
     *
     * Updates Redis cache entry
     * for this eventId.
     */
    @CachePut(
            value = "events",
            key = "#eventId"
    )
    public EventResponse updateEvent(

            Long eventId,

            EventUpdateRequest request) {

        Event event =

                eventRepository
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

        event.setVenue(
                request.getVenue()
        );

        event.setLanguage(
                request.getLanguage()
        );

        event.setType(

                EventType.valueOf(

                        request.getType()
                                .toUpperCase()
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

        /*
         * Re-sync the updated document to Elasticsearch.
         */
        eventSearchService.indexEvent(updatedEvent);

        return mapToResponse(updatedEvent);
    }

    /*
     * Delete event.
     *
     * Evicts cache entry for
     * this eventId.
     */
    @CacheEvict(
            value = "events",
            key = "#eventId"
    )
    public void deleteEvent(
            Long eventId) {

        Event event =

                eventRepository
                        .findById(eventId)

                        .orElseThrow(() ->

                                new ResourceNotFoundException(

                                        "Event not found with ID: "
                                                + eventId
                                )
                        );

        /*
         * Delete children in FK-safe order:
         *
         * booking_seats (→ bookings, → seats)
         * payments      (→ bookings)
         * bookings      (→ shows)
         * seat_locks    (show column)
         * seats         (→ shows)
         * shows         (→ events)
         * event
         */
        List<Show> shows =
                showRepository.findByEventId(eventId);

        for (Show show : shows) {

            List<Booking> bookings =
                    bookingRepository.findByShowId(show.getId());

            for (Booking booking : bookings) {
                bookingSeatRepository.deleteAllByBookingId(booking.getId());
                paymentRepository.deleteAllByBookingId(booking.getId());
            }

            bookingRepository.deleteAll(bookings);

            seatLockRepository.deleteAllByShowId(show.getId());

            seatRepository.deleteAll(
                    seatRepository.findByShowId(show.getId()));
        }

        showRepository.deleteAll(shows);

        eventRepository.delete(event);

        /*
         * Remove the corresponding document from
         * Elasticsearch so it stops showing in search.
         */
        eventSearchService.deleteEvent(eventId);
    }

    /*
     * Entity -> DTO mapper.
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

        response.setVenue(
                event.getVenue()
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