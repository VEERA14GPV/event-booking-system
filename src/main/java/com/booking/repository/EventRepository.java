package com.booking.repository;

import com.booking.entity.Event;

import com.booking.enums.EventType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository
        .JpaSpecificationExecutor;

import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository   extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    Page<Event> findByType(
            EventType type,
            Pageable pageable
    );

    Page<Event> findByCity(
            String city,
            Pageable pageable
    );

    Page<Event> findByLanguage(
            String language,
            Pageable pageable
    );
}