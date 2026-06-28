package com.booking.controller;

import com.booking.dto.request.SeatLockRequest;
import com.booking.entity.Seat;
import com.booking.exception.SeatLockException;
import com.booking.locking.SeatLockManager;
import com.booking.service.SeatService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    private final SeatLockManager seatLockManager;

    @GetMapping("/show/{showId}")
    public ResponseEntity<List<Seat>> getSeatsByShow(@PathVariable Long showId) {

        return ResponseEntity.ok(seatService.getSeatsByShow(showId));
    }

    @PostMapping("/lock")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> lockSeat(@Valid @RequestBody SeatLockRequest request) {

        try {

            seatLockManager.lockSeat(
                    request.getShowId(),
                    request.getSeatId(),
                    request.getUserId());

            return ResponseEntity.ok("Seat locked successfully");

        } catch (SeatLockException ex) {

            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/unlock")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> unlockSeat(
            @RequestParam Long showId,
            @RequestParam Long seatId) {

        seatLockManager.unlockSeat(showId, seatId);

        return ResponseEntity.ok("Seat unlocked successfully");
    }

    @GetMapping("/status")
    public ResponseEntity<Boolean> isSeatLocked(
            @RequestParam Long showId,
            @RequestParam Long seatId) {

        return ResponseEntity.ok(seatLockManager.isSeatLocked(showId, seatId));
    }
}
