package com.booking.entity;

import jakarta.persistence.*;

@Entity
public class BookingSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Booking booking;

    @ManyToOne
    private Seat seat;

    public Long getId() 
    {
    	return id;
    }
    public Booking getBooking() 
    {
    	return booking;
    }
    public Seat getSeat()
    {
    	return seat;
    }

    
    public void setId(Long id) 
    {
    	this.id = id;
    }
    public void setBooking(Booking booking) 
    {
    	this.booking = booking; 
    }
    public void setSeat(Seat seat) 
    {
    	this.seat = seat;
    }
}