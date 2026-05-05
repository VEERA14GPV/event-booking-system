package com.booking.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.booking.entity.Show;
import com.booking.repository.ShowRepository;

@Service
public class ShowService {

    @Autowired
    private ShowRepository showRepository;

    public Show createShow(Show show) {
        return showRepository.save(show);
    }

    public List<Show> getAllShows() {
        return showRepository.findAll();
    }

    public List<Show> getShowsByEvent(Long eventId) {
        return showRepository.findByEventId(eventId);
    }
}