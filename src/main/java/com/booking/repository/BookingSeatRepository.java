package com.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booking.entity.BookingSeat;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {
}