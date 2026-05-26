package com.booking.service.ttl;

import com.booking.entity.Seat;

import com.booking.enums.SeatStatus;

import com.booking.repository.SeatRepository;

import org.springframework.stereotype.Service;

@Service
public class SeatUnlockService {

    private final SeatRepository seatRepository;

    public SeatUnlockService(
            SeatRepository seatRepository) {

        this.seatRepository = seatRepository;
    }

    public void unlockSeat(Long seatId) {

        Seat seat =
                seatRepository.findById(seatId)
                        .orElse(null);

        if (seat != null) {

            seat.setStatus(
                    SeatStatus.AVAILABLE
            );

            seatRepository.save(seat);
        }
    }
}