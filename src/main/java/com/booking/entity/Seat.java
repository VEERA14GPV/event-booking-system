package com.booking.entity;

import com.booking.enums.SeatStatus;
import jakarta.persistence.*;

@Entity
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String seatNumber;

    @ManyToOne
    private Show show;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    // getters & setters
    public Long getId() { return id; }
    public String getSeatNumber() { return seatNumber; }
    public Show getShow() { return show; }
    public SeatStatus getStatus() { return status; }

    public void setId(Long id) { this.id = id; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public void setShow(Show show) { this.show = show; }
    public void setStatus(SeatStatus status) { this.status = status; }
}