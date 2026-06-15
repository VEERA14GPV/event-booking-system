package com.booking.entity;

import com.booking.enums.SeatStatus;

import jakarta.persistence.*;

@Entity
@Table(name = "seats")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String seatNumber;

    private String seatType;

    @ManyToOne
    @JoinColumn(name = "show_id")
    private Show show;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    @Version
    private Integer version;

    public Long getId() {
        return id;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public String getSeatType() {
        return seatType;
    }

    public Show getShow() {
        return show;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public Integer getVersion() {
        return version;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public void setShow(Show show) {
        this.show = show;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}