package com.booking.service.cache;

import com.booking.entity.Seat;

import com.booking.repository.SeatRepository;

import org.springframework.cache.annotation.Cacheable;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeatAvailabilityCacheService {

    private final SeatRepository seatRepository;

    public SeatAvailabilityCacheService(
            SeatRepository seatRepository) {

        this.seatRepository = seatRepository;
    }

    @Cacheable(
            value = "seats",
            key = "#showId"
    )
    public List<Seat> getSeatsByShow(
            Long showId) {

        return seatRepository.findByShowId(showId);
    }
}