package com.booking.entity;

import com.booking.enums.EventType;
import jakarta.persistence.*;

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private EventType type;

    private Integer duration;

    public Long getId() 
    {
    	return id;
    }
    public String getName() 
    {
    	return name;
    }
    public EventType getType() 
    {
    	return type;
    }
    public Integer getDuration() 
    {
    	return duration;
    }

    public void setId(Long id) 
    {
    	this.id = id;
    }
    public void setName(String name) 
    {
    	this.name = name;
    }
    public void setType(EventType type) 
    {
    	this.type = type;
    }
    public void setDuration(Integer duration) 
    {
    	this.duration = duration; 
    }
}