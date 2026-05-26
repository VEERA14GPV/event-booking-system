package com.booking.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class BookingRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Show ID is required")
    private Long showId;

    @NotEmpty(message = "Seat IDs cannot be empty")
    private List<Long> seatIds;

    public BookingRequest() {
    }

    public Long getUserId() {

        return userId;
    }

    public void setUserId(Long userId) {

        this.userId = userId;
    }

    public Long getShowId() {

        return showId;
    }

    public void setShowId(Long showId) {

        this.showId = showId;
    }

    public List<Long> getSeatIds() {

        return seatIds;
    }

    public void setSeatIds(List<Long> seatIds) {

        this.seatIds = seatIds;
    }
}