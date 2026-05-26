package com.booking.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Component;

@Component
public class PaginationUtil {

    /*
     * Create pageable object
     */
    public Pageable createPageable(

            int page,
            int size,
            String sortBy,
            String direction) {

        Sort sort = direction.equalsIgnoreCase(
                "desc"
        )

                ? Sort.by(sortBy).descending()

                : Sort.by(sortBy).ascending();

        return PageRequest.of(
                page,
                size,
                sort
        );
    }
}