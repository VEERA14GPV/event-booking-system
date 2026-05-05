package com.booking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.booking.entity.Show;
import com.booking.service.ShowService;

@RestController
@RequestMapping("/shows")
public class ShowController {

    @Autowired
    private ShowService showService;

    @PostMapping
    public Show createShow(@RequestBody Show show) {
        return showService.createShow(show);
    }

    @GetMapping
    public List<Show> getAllShows() {
        return showService.getAllShows();
    }

    @GetMapping("/event/{eventId}")
    public List<Show> getShowsByEvent(@PathVariable Long eventId) {
        return showService.getShowsByEvent(eventId);
    }
}
