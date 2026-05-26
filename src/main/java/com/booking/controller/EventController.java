package com.booking.controller;

import com.booking.dto.request.EventCreateRequest;
import com.booking.dto.request.EventUpdateRequest;

import com.booking.dto.response.EventResponse;

import com.booking.enums.EventType;

import com.booking.service.EventService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZER')")
    @CacheEvict(value = "events", allEntries = true)
    public ResponseEntity<EventResponse>
    createEvent(
            @Valid
            @RequestBody
            EventCreateRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        eventService.createEvent(
                                request
                        )
                );
    }

    @GetMapping
    public ResponseEntity<Page<EventResponse>>
    getAllEvents(

            Pageable pageable,

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

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse>
    getEventById(
            @PathVariable Long eventId) {

        return ResponseEntity.ok(

                eventService.getEventById(
                        eventId
                )
        );
    }

    @PutMapping("/{eventId}")
    @PreAuthorize("hasRole('ORGANIZER')")
    @CacheEvict(value = "events", allEntries = true)
    public ResponseEntity<EventResponse>
    updateEvent(

            @PathVariable Long eventId,

            @Valid
            @RequestBody
            EventUpdateRequest request) {

        return ResponseEntity.ok(

                eventService.updateEvent(
                        eventId,
                        request
                )
        );
    }

    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasAnyRole('ORGANIZER','ADMIN')")
    @CacheEvict(value = "events", allEntries = true)
    public ResponseEntity<String>
    deleteEvent(
            @PathVariable Long eventId) {

        eventService.deleteEvent(
                eventId
        );

        return ResponseEntity.ok(
                "Event deleted successfully"
        );
    }
}