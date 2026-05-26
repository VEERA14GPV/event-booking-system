package com.booking.service;

import com.booking.entity.Show;

import com.booking.exception.ResourceNotFoundException;

import com.booking.repository.ShowRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShowService {

    private final ShowRepository showRepository;

    public ShowService(
            ShowRepository showRepository) {

        this.showRepository =
                showRepository;
    }

    /*
     * Get all shows
     */
    public List<Show> getAllShows() {

        return showRepository.findAll();
    }

    /*
     * Get show by ID
     */
    public Show getShowById(
            Long showId) {

        return showRepository.findById(
                showId
        )
        .orElseThrow(() ->

                new ResourceNotFoundException(
                        "Show not found"
                )
        );
    }

    /*
     * Create show
     */
    public Show createShow(
            Show show) {

        return showRepository.save(
                show
        );
    }

    /*
     * Delete show
     */
    public void deleteShow(
            Long showId) {

        Show show =
                getShowById(showId);

        showRepository.delete(show);
    }
}