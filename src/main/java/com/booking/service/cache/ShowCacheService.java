package com.booking.service.cache;

import com.booking.entity.Show;
import com.booking.exception.ResourceNotFoundException;
import com.booking.repository.ShowRepository;

import org.springframework.cache.annotation.Cacheable;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShowCacheService {

    private final ShowRepository showRepository;

    public ShowCacheService(
            ShowRepository showRepository) {

        this.showRepository = showRepository;
    }

    @Cacheable(value = "shows")
    public List<Show> getAllShows() {

        return showRepository.findAll();
    }

    @Cacheable(value = "shows", key = "#showId")
    public Show getShowById(Long showId) {
        return showRepository.findById(showId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Show not found with ID: " + showId
                    )
                );  
    }
}
