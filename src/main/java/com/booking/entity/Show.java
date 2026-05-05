package com.booking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "shows")
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Event event;

    private LocalDateTime startTime;


    public Long getId() 
    {
    	return id; 
    }
    public Event getEvent()
    {
    	return event;
    }
    public LocalDateTime getStartTime() 
    {
    	return startTime; 
    }
    
    

    public void setId(Long id) 
    {
    	this.id = id;
    }
    public void setEvent(Event event) 
    {
    	this.event = event;
    }
    public void setStartTime(LocalDateTime startTime) 
    {
    	this.startTime = startTime;
    }
}