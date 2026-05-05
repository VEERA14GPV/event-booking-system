package com.booking.entity;

import com.booking.enums.BookingStatus;
import jakarta.persistence.*;

@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne
    private Show show;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    
    public Long getId() 
    { 
    	return id;
    }
    public Long getUserId() 
    {
    	return userId;
    }
    public Show getShow() 
    {
    	return show;
    }
    public BookingStatus getStatus() 
    {
    	return status;
    }

    
    
    public void setId(Long id) 
    {
    	this.id = id;
    }
    public void setUserId(Long userId) 
    {
    	this.userId = userId;
    }
    public void setShow(Show show) 
    {
    	this.show = show;
    }
    public void setStatus(BookingStatus status) 
    {
    	this.status = status;
    }
}