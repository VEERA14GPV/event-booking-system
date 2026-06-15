package com.booking.controller;

import com.booking.dto.request.EventCreateRequest;

import com.booking.dto.request.EventUpdateRequest;

import com.booking.dto.response.EventResponse;

import com.booking.enums.EventType;

import com.booking.service.EventService;

import com.booking.service.authorization.EventOwnershipService;

import com.booking.util.SecurityUtil;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    private final EventOwnershipService
            eventOwnershipService;

    private final SecurityUtil securityUtil;

    /*
     * Allowed sorting fields.
     *
     * Prevents invalid or unsafe sorting.
     */
    private static final List<String>
            ALLOWED_SORT_FIELDS = List.of(

                    "price",

                    "rating",

                    "name",

                    "createdAt",

                    "city"
            );

    /*
     * Create event.
     *
     * Cache eviction handled
     * in EventService.
     */
    @PostMapping
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<EventResponse>
    createEvent(

            @Valid
            @RequestBody
            EventCreateRequest request) {

        return ResponseEntity

                .status(HttpStatus.CREATED)

                .body(

                        eventService.createEvent(request)
                );
    }

    /*
     * Get all events.
     *
     * Supports pagination,
     * dynamic filtering,
     * and server-side sorting.
     */
    @GetMapping
    public ResponseEntity<Page<EventResponse>>
    getAllEvents(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "createdAt")
            String sortBy,

            @RequestParam(defaultValue = "desc")
            String direction,

            @RequestParam(required = false)
            String city,

            @RequestParam(required = false)
            String language,

            @RequestParam(required = false)
            EventType type,

            @RequestParam(required = false)
            Double rating,

            @RequestParam(required = false)
            Double price) {

        /*
         * Reject invalid sort fields.
         */
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {

            throw new RuntimeException(
                    "Invalid sorting field: " + sortBy
            );
        }

        Pageable pageable =

                PageRequest.of(

                        page,

                        size,

                        Sort.by(

                                Sort.Direction.fromString(
                                        direction
                                ),

                                sortBy
                        )
                );

        return ResponseEntity.ok(

                eventService.getAllEvents(

                        pageable,

                        city,

                        language,

                        type,

                        rating,

                        price
                )
        );
    }

    /*
     * Get event by ID.
     *
     * Response served from Redis
     * cache after first hit.
     */
    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse>
    getEventById(

            @PathVariable Long eventId) {

        return ResponseEntity.ok(

                eventService.getEventById(eventId)
        );
    }

    /*
     * Update event.
     *
     * Cache update handled
     * in EventService.
     */
    @PutMapping("/{eventId}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<EventResponse>
    updateEvent(

            @PathVariable Long eventId,

            @Valid
            @RequestBody
            EventUpdateRequest request) {

        Long currentUserId =
                securityUtil.getCurrentUserId();

        /*
         * Validate ownership.
         */
        if (currentUserId != null) {

            eventOwnershipService
                    .validateEventOwnership(

                            eventId,

                            currentUserId
                    );
        }

        return ResponseEntity.ok(

                eventService.updateEvent(
                        eventId,
                        request
                )
        );
    }

    /*
     * Delete event.
     *
     * Cache eviction handled
     * in EventService.
     */
    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasAnyRole('ORGANIZER','ADMIN')")
    public ResponseEntity<String>
    deleteEvent(

            @PathVariable Long eventId) {

        Long currentUserId =
                securityUtil.getCurrentUserId();

        /*
         * Organizer can only delete
         * their own events.
         */
        if (currentUserId != null

                && securityUtil.hasRole(
                "ROLE_ORGANIZER")) {

            eventOwnershipService
                    .validateEventOwnership(

                            eventId,

                            currentUserId
                    );
        }

        eventService.deleteEvent(eventId);

        return ResponseEntity.ok(
                "Event deleted successfully"
        );
    }
}