package com.booking.service;

import com.booking.entity.Seat;

import com.booking.enums.SeatStatus;

import com.booking.exception.ResourceNotFoundException;

import com.booking.repository.SeatRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeatService {

    private final SeatRepository seatRepository;

    public SeatService(
            SeatRepository seatRepository) {

        this.seatRepository =
                seatRepository;
    }

    /*
     * Get seats by show
     */
    public List<Seat> getSeatsByShow(
            Long showId) {

        return seatRepository.findByShowId(
                showId
        );
    }

    /*
     * Get available seats
     */
    public List<Seat> getAvailableSeats(
            Long showId) {

        return seatRepository
                .findByShowIdAndStatus(
                        showId,
                        SeatStatus.AVAILABLE
                );
    }

    /*
     * Get seat by ID
     */
    public Seat getSeatById(
            Long seatId) {

        return seatRepository.findById(
                seatId
        )
        .orElseThrow(() ->

                new ResourceNotFoundException(
                        "Seat not found"
                )
        );
    }

    /*
     * Create seat
     */
    public Seat createSeat(
            Seat seat) {

        seat.setStatus(
                SeatStatus.AVAILABLE
        );

        return seatRepository.save(
                seat
        );
    }

    /*
     * Delete seat
     */
    public void deleteSeat(
            Long seatId) {

        Seat seat =
                getSeatById(seatId);

        seatRepository.delete(seat);
    }
}