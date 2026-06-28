package com.booking.repository;

import com.booking.entity.Event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.jpa.repository
        .JpaSpecificationExecutor;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository   extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    /*
     * MySQL full-text fallback search.
     *
     * Used by EventSearchService when Elasticsearch
     * is unavailable (Resilience4j fallback).
     *
     * Searches across name, description, category (type)
     * and venue.
     */
    @Query("SELECT e FROM Event e WHERE "
            + "LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
            + "LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
            + "LOWER(CAST(e.type AS string)) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
            + "LOWER(e.venue) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Event> searchByKeyword(
            @Param("keyword") String keyword,
            Pageable pageable);
}