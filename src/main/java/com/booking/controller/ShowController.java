package com.booking.controller;

import com.booking.dto.request.ShowCreateRequest;
import com.booking.entity.Show;
import com.booking.service.ShowService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shows")
@RequiredArgsConstructor
public class ShowController {

    private final ShowService showService;

    @GetMapping
    public ResponseEntity<List<Show>> getAllShows() {
        return ResponseEntity.ok(
                showService.getAllShows()
        );
    }

    @GetMapping("/{showId}")
    public ResponseEntity<Show> getShowById(@PathVariable Long showId) {
        return ResponseEntity.ok(
                showService.getShowById(showId)
        );
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Show>> getShowsByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(
                showService.getShowsByEvent(eventId)
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ORGANIZER','ADMIN')")
    public ResponseEntity<Show> createShow(
            @Valid @RequestBody ShowCreateRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(showService.createShow(request));
    }

    /*
     * Create multiple shows in one request.
     * e.g. same event, several different time slots.
     */
    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('ORGANIZER','ADMIN')")
    public ResponseEntity<List<Show>> createShows(
            @Valid @RequestBody List<ShowCreateRequest> requests) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(showService.createShows(requests));
    }

    @DeleteMapping("/{showId}")
    @PreAuthorize("hasAnyRole('ORGANIZER','ADMIN')")
    public ResponseEntity<String> deleteShow(@PathVariable Long showId) {

        showService.deleteShow(showId);

        return ResponseEntity.ok("Show deleted successfully");
    }
}