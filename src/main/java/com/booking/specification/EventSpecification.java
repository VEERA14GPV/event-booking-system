package com.booking.specification;

import com.booking.entity.Event;
import com.booking.enums.EventType;
import org.springframework.data.jpa.domain.Specification;

public class EventSpecification {

    public static Specification<Event> hasCity(
            String city) {

        return (root, query, criteriaBuilder) ->

                city == null
                        ? null
                        : criteriaBuilder.equal(
                                root.get("city"),
                                city
                );
    }

    public static Specification<Event> hasLanguage(
            String language) {

        return (root, query, criteriaBuilder) ->

                language == null
                        ? null
                        : criteriaBuilder.equal(
                                root.get("language"),
                                language
                );
    }

    public static Specification<Event> hasType(
            EventType type) {

        return (root, query, criteriaBuilder) ->

                type == null
                        ? null
                        : criteriaBuilder.equal(
                                root.get("type"),
                                type
                );
    }

    public static Specification<Event> hasMinimumRating(
            Double rating) {

        return (root, query, criteriaBuilder) ->

                rating == null
                        ? null
                        : criteriaBuilder.greaterThanOrEqualTo(
                                root.get("rating"),
                                rating
                );
    }

    public static Specification<Event> hasMaximumPrice(
            Double price) {

        return (root, query, criteriaBuilder) ->

                price == null
                        ? null
                        : criteriaBuilder.lessThanOrEqualTo(
                                root.get("price"),
                                price
                );
    }
}