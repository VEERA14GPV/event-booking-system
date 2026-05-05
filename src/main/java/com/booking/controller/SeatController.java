package com.booking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.booking.entity.Seat;
import com.booking.service.SeatService;

@RestController
@RequestMapping("/seats")
public class SeatController {

    @Autowired
    private SeatService seatService;

    @GetMapping("/show/{showId}")
    public List<Seat> getSeatsByShow(@PathVariable Long showId) {
        return seatService.getSeatsByShow(showId);
    }

    // Optional: create seats manually
    @PostMapping
    public List<Seat> createSeats(@RequestBody List<Seat> seats) {
        return seatService.saveAllSeats(seats);
    }
}