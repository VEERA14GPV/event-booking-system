package com.booking.controller;

import com.booking.entity.Show;

import com.booking.service.ShowService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shows")
@RequiredArgsConstructor
public class ShowController {

    private final ShowService showService;

    @GetMapping
    public ResponseEntity<List<Show>>
    getAllShows() {

        return ResponseEntity.ok(
                showService.getAllShows()
        );
    }

    @GetMapping("/{showId}")
    public ResponseEntity<Show>
    getShowById(@PathVariable Long showId) {

        return ResponseEntity.ok(

                showService.getShowById(
                        showId
                )
        );
    }
}