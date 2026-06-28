package com.booking.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ShowCreateRequest {

    @NotNull
    private Long eventId;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private Double price;

    private List<SeatConfigItem> seats = new ArrayList<>();

    public ShowCreateRequest() {
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public List<SeatConfigItem> getSeats() {
        return seats;
    }

    public void setSeats(List<SeatConfigItem> seats) {
        this.seats = seats != null ? seats : new ArrayList<>();
    }
}