package com.booking.service.search;

import com.booking.document.EventDocument;
import com.booking.dto.response.EventResponse;
import com.booking.entity.Event;
import com.booking.repository.EventRepository;
import com.booking.repository.elasticsearch.EventSearchRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventSearchService {

    private static final Logger log =
            LoggerFactory.getLogger(EventSearchService.class);

    private final EventSearchRepository eventSearchRepository;
    private final EventRepository eventRepository;

    @Retry(
            name = "elasticsearchIndex",
            fallbackMethod = "indexEventFallback"
    )
    @CircuitBreaker(
            name = "elasticsearchIndex",
            fallbackMethod = "indexEventFallback"
    )
    public void indexEvent(Event event) {

        eventSearchRepository.save(toDocument(event));
    }

    public void indexEventFallback(
            Event event,
            Throwable throwable) {

        log.warn(
                "Elasticsearch indexing failed for event {} : {}",
                event.getId(),
                throwable.getMessage()
        );
    }

    @Retry(
            name = "elasticsearchIndex",
            fallbackMethod = "deleteEventFallback"
    )
    @CircuitBreaker(
            name = "elasticsearchIndex",
            fallbackMethod = "deleteEventFallback"
    )
    public void deleteEvent(Long eventId) {

        eventSearchRepository.deleteById(
                String.valueOf(eventId)
        );
    }

    public void deleteEventFallback(
            Long eventId,
            Throwable throwable) {

        log.warn(
                "Elasticsearch delete failed for event {} : {}",
                eventId,
                throwable.getMessage()
        );
    }

    @Retry(
            name = "elasticsearchSearch",
            fallbackMethod = "searchEventsFallback"
    )
    @CircuitBreaker(
            name = "elasticsearchSearch",
            fallbackMethod = "searchEventsFallback"
    )
    public Page<EventResponse> searchEvents(
            String keyword,
            Pageable pageable) {

        return eventSearchRepository
                .searchByKeyword(keyword, pageable)
                .map(this::toResponse);
    }

    public Page<EventResponse> searchEventsFallback(
            String keyword,
            Pageable pageable,
            Throwable throwable) {

        log.warn(
                "Elasticsearch unavailable. Falling back to MySQL. {}",
                throwable.getMessage()
        );

        return eventRepository
                .searchByKeyword(keyword, pageable)
                .map(this::toResponseFromEntity);
    }

    private EventDocument toDocument(Event event) {

        EventDocument document = new EventDocument();

        document.setId(String.valueOf(event.getId()));
        document.setName(event.getName());
        document.setDescription(event.getDescription());

        document.setCategory(
                event.getType() != null
                        ? event.getType().name()
                        : null
        );

        document.setVenue(event.getVenue());
        document.setCity(event.getCity());
        document.setLanguage(event.getLanguage());
        document.setRating(event.getRating());
        document.setPrice(event.getPrice());
        document.setCreatedAt(event.getCreatedAt());

        return document;
    }

    private EventResponse toResponse(EventDocument document) {

        EventResponse response = new EventResponse();

        response.setEventId(Long.valueOf(document.getId()));
        response.setName(document.getName());
        response.setDescription(document.getDescription());
        response.setCity(document.getCity());
        response.setVenue(document.getVenue());
        response.setLanguage(document.getLanguage());

        if (document.getCategory() != null) {
            response.setType(
                    com.booking.enums.EventType.valueOf(
                            document.getCategory()
                    )
            );
        }

        response.setRating(document.getRating());
        response.setPrice(document.getPrice());
        response.setCreatedAt(document.getCreatedAt());

        return response;
    }

    private EventResponse toResponseFromEntity(Event event) {

        EventResponse response = new EventResponse();

        response.setEventId(event.getId());
        response.setName(event.getName());
        response.setDescription(event.getDescription());
        response.setCity(event.getCity());
        response.setVenue(event.getVenue());
        response.setLanguage(event.getLanguage());
        response.setType(event.getType());
        response.setRating(event.getRating());
        response.setPrice(event.getPrice());
        response.setCreatedAt(event.getCreatedAt());

        return response;
    }
}