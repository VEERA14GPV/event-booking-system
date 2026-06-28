package com.booking.specification;

import com.booking.entity.Event;
import com.booking.enums.EventType;

import org.springframework.data.jpa.domain.Specification;

public class EventSpecification {

    public static Specification<Event> hasCity(
            String city) {

        return (root, query, criteriaBuilder) ->

                city == null || city.isBlank()

                        ? criteriaBuilder.conjunction()

                        : criteriaBuilder.like(

                                criteriaBuilder.lower(
                                        root.get("city")
                                ),

                                "%" + city.toLowerCase() + "%"
                        );
    }

    public static Specification<Event> hasLanguage(
            String language) {

        return (root, query, criteriaBuilder) ->

                language == null || language.isBlank()

                        ? criteriaBuilder.conjunction()

                        : criteriaBuilder.like(

                                criteriaBuilder.lower(
                                        root.get("language")
                                ),

                                "%" + language.toLowerCase() + "%"
                        );
    }

    public static Specification<Event> hasVenue(
            String venue) {

        return (root, query, criteriaBuilder) ->

                venue == null || venue.isBlank()

                        ? criteriaBuilder.conjunction()

                        : criteriaBuilder.like(

                                criteriaBuilder.lower(
                                        root.get("venue")
                                ),

                                "%" + venue.toLowerCase() + "%"
                        );
    }

    public static Specification<Event> hasType(
            EventType type) {

        return (root, query, criteriaBuilder) ->

                type == null

                        ? criteriaBuilder.conjunction()

                        : criteriaBuilder.equal(
                                root.get("type"),
                                type
                        );
    }

    public static Specification<Event>
    hasMinimumRating(
            Double rating) {

        return (root, query, criteriaBuilder) ->

                rating == null

                        ? criteriaBuilder.conjunction()

                        : criteriaBuilder
                        .greaterThanOrEqualTo(
                                root.get("rating"),
                                rating
                        );
    }

    public static Specification<Event>
    hasMaximumPrice(
            Double price) {

        return (root, query, criteriaBuilder) ->

                price == null

                        ? criteriaBuilder.conjunction()

                        : criteriaBuilder
                        .lessThanOrEqualTo(
                                root.get("price"),
                                price
                        );
    }
}