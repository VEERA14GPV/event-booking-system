package com.booking.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.booking.entity.Seat;
import com.booking.repository.SeatRepository;

@Service
public class SeatService {

    @Autowired
    private SeatRepository seatRepository;

    public List<Seat> getSeatsByShow(Long showId) {
        if (showId == null) {
            throw new RuntimeException("Show ID cannot be null");
        }
        return seatRepository.findByShowId(showId);
    }

    public List<Seat> saveAllSeats(List<Seat> seats) {
        if (seats == null || seats.isEmpty()) {
            throw new RuntimeException("Seat list cannot be empty");
        }
        return seatRepository.saveAll(seats);
    }
}