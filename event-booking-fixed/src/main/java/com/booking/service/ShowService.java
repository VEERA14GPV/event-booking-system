package com.booking.service;

import com.booking.entity.Show;

import com.booking.exception.ResourceNotFoundException;

import com.booking.repository.ShowRepository;

import com.booking.service.cache.ShowCacheService;

import org.springframework.cache.annotation.CacheEvict;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ShowService {

    private final ShowRepository
            showRepository;

    /*
     * Redis cache service.
     */
    private final ShowCacheService
            showCacheService;

    public ShowService(

            ShowRepository
                    showRepository,

            ShowCacheService
                    showCacheService) {

        this.showRepository =
                showRepository;

        this.showCacheService =
                showCacheService;
    }

    /*
     * Get all shows
     *
     * Served from Redis cache.
     */
    @Transactional(readOnly = true)
    public List<Show> getAllShows() {

        return showCacheService
                .getAllShows();
    }

    /*
     * Get show by ID
     *
     * Served from Redis cache.
     */
    @Transactional(readOnly = true)
    public Show getShowById(
            Long showId) {

        Show show =

                showCacheService
                        .getShowById(showId);

        if (show == null) {

            throw new ResourceNotFoundException(
                    "Show not found"
            );
        }

        return show;
    }

    /*
     * Create show
     *
     * Clears Redis cache.
     */
    @CacheEvict(
            value = "shows",
            allEntries = true
    )
    public Show createShow(
            Show show) {

        return showRepository.save(show);
    }

    /*
     * Delete show
     *
     * Clears Redis cache.
     */
    @CacheEvict(
            value = "shows",
            allEntries = true
    )
    public void deleteShow(
            Long showId) {

        Show show =
                getShowById(showId);

        showRepository.delete(show);
    }
}
