package com.booking.entity;

import com.booking.enums.EventType;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 2000)
    private String description;

    private String city;

    private String language;

    private Double rating;

    private Double price;

    @Enumerated(EnumType.STRING)
    private EventType type;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private User organizer;

    private LocalDateTime createdAt;

    public Long getId() {
        return id;
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

    public Double getRating() {
        return rating;
    }

    public Double getPrice() {
        return price;
    }

    public EventType getType() {
        return type;
    }

    public User getOrganizer() {
        return organizer;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt = createdAt;
    }
}