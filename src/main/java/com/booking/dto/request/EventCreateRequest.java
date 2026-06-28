package com.booking.dto.request;

import com.booking.enums.EventType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EventCreateRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotBlank
    private String city;

    @NotBlank
    private String venue;

    @NotBlank
    private String language;

    @NotNull
    private EventType type;

    @NotNull
    private Double rating;

    @NotNull
    private Double price;

    public EventCreateRequest() {
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

    public String getVenue() {
        return venue;
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

    public void setVenue(String venue) {
        this.venue = venue;
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
}