package com.booking.dto.response;

import com.booking.enums.EventType;

import java.time.LocalDateTime;

public class EventResponse {

    private Long eventId;

    private String name;

    private String description;

    private String city;

    private String language;

    private EventType type;

    private Double rating;

    private Double price;

    private LocalDateTime createdAt;

    public EventResponse() {
    }

    public Long getEventId() {
        return eventId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCity() {
        return city;
    }

    public String getLanguage() {
        return language;
    }

    public EventType getType() {
        return type;
    }

    public Double getRating() {
        return rating;
    }

    public Double getPrice() {
        return price;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(
            String description) {

        this.description = description;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setLanguage(
            String language) {

        this.language = language;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt = createdAt;
    }
}
