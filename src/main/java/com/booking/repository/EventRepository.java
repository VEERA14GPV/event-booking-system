package com.booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booking.entity.Event;
import com.booking.enums.EventType;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByType(EventType type);
}