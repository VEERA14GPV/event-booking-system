package com.booking.service;

import com.booking.dto.request.SeatConfigItem;
import com.booking.dto.request.ShowCreateRequest;
import com.booking.dto.websocket.SeatUpdateMessage;
import com.booking.entity.Event;
import com.booking.entity.Seat;
import com.booking.entity.Show;
import com.booking.enums.SeatStatus;
import com.booking.exception.ResourceNotFoundException;
import com.booking.repository.EventRepository;
import com.booking.repository.SeatRepository;
import com.booking.repository.ShowRepository;
import com.booking.service.authorization.EventOwnershipService;
import com.booking.service.cache.ShowCacheService;
import com.booking.service.websocket.SeatBroadcastService;
import com.booking.util.SecurityUtil;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ShowService {

    private final ShowRepository showRepository;
    private final ShowCacheService showCacheService;
    private final EventRepository eventRepository;
    private final EventOwnershipService eventOwnershipService;
    private final SecurityUtil securityUtil;
    private final SeatRepository seatRepository;
    private final SeatBroadcastService seatBroadcastService;

    public ShowService(
            ShowRepository showRepository,
            ShowCacheService showCacheService,
            EventRepository eventRepository,
            EventOwnershipService eventOwnershipService,
            SecurityUtil securityUtil,
            SeatRepository seatRepository,
            SeatBroadcastService seatBroadcastService) {

        this.showRepository = showRepository;
        this.showCacheService = showCacheService;
        this.eventRepository = eventRepository;
        this.eventOwnershipService = eventOwnershipService;
        this.securityUtil = securityUtil;
        this.seatRepository = seatRepository;
        this.seatBroadcastService = seatBroadcastService;
    }

    /*
     * Get all shows
     */
    @Transactional(readOnly = true)
    public List<Show> getAllShows() {

        return showCacheService.getAllShows();
    }

    /*
     * Get show by ID
     */
    @Transactional(readOnly = true)
    public Show getShowById(Long showId) {

        return showCacheService.getShowById(showId);
    }

    /*
     * Get shows for a specific event
     */
    @Transactional(readOnly = true)
    public List<Show> getShowsByEvent(Long eventId) {

        return showRepository.findByEventId(eventId);
    }

    /*
     * Create show.
     *
     * Validates that the current organizer owns
     * the event before attaching a show to it.
     */
    @CacheEvict(value = "shows", allEntries = true)
    public Show createShow(ShowCreateRequest request) {

        Long currentUserId = securityUtil.getCurrentUserId();

        boolean isAdmin = currentUserId != null &&
                SecurityContextHolder.getContext().getAuthentication()
                        .getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (currentUserId != null && !isAdmin) {
            eventOwnershipService.validateEventOwnership(
                    request.getEventId(),
                    currentUserId
            );
        }

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Event not found with ID: " + request.getEventId()
                        )
                );

        Show show = new Show();
        show.setEvent(event);
        show.setStartTime(request.getStartTime());
        show.setPrice(request.getPrice());

        Show savedShow = showRepository.save(show);

        List<SeatConfigItem> seatItems = request.getSeats();
        if (seatItems != null && !seatItems.isEmpty()) {
            for (SeatConfigItem item : seatItems) {
                Seat seat = new Seat();
                seat.setShow(savedShow);
                seat.setSeatNumber(item.getSeatNumber());
                seat.setSeatType(item.getSeatType());
                seat.setStatus(SeatStatus.AVAILABLE);
                Seat savedSeat = seatRepository.save(seat);

                SeatUpdateMessage msg = new SeatUpdateMessage();
                msg.setShowId(savedShow.getId());
                msg.setSeatId(savedSeat.getId());
                msg.setStatus("AVAILABLE");
                seatBroadcastService.broadcastSeatUpdate(msg);
            }
            seatBroadcastService.broadcastLayoutUpdate(savedShow.getId());
        }

        return savedShow;
    }

    /*
     * Create multiple shows for one or more events
     * in a single call (e.g. several time slots for
     * the same movie/event).
     */
    @CacheEvict(value = "shows", allEntries = true)
    public List<Show> createShows(List<ShowCreateRequest> requests) {

        return requests.stream()
                .map(this::createShow)
                .toList();
    }

    /*
     * Delete show
     */
    @CacheEvict(value = "shows", allEntries = true)
    public void deleteShow(Long showId) {

        Show show = showCacheService.getShowById(showId);

        Long currentUserId = securityUtil.getCurrentUserId();
        if (currentUserId != null && !securityUtil.hasRole("ROLE_ADMIN")) {
            eventOwnershipService.validateEventOwnership(
                    show.getEvent().getId(), currentUserId);
        }

        showRepository.delete(show);
    }
}