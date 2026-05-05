package com.booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booking.entity.Show;

public interface ShowRepository extends JpaRepository<Show, Long> {

    List<Show> findByEventId(Long eventId);
}