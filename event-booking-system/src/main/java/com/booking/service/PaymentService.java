package com.booking.service;

import com.booking.dto.request.PaymentRequest;
import com.booking.dto.response.PaymentResponse;
import com.booking.dto.websocket.SeatUpdateMessage;

import com.booking.entity.Booking;
import com.booking.entity.BookingSeat;
import com.booking.entity.Payment;
import com.booking.entity.Seat;

import com.booking.enums.BookingStatus;
import com.booking.enums.PaymentStatus;

import com.booking.exception.ResourceNotFoundException;

import com.booking.locking.SeatLockManager;

import com.booking.payment.RazorpayService;

import com.booking.repository.BookingRepository;
import com.booking.repository.BookingSeatRepository;
import com.booking.repository.PaymentRepository;

import com.booking.service.websocket.SeatBroadcastService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final BookingRepository bookingRepository;

    private final BookingSeatRepository bookingSeatRepository;

    private final RazorpayService razorpayService;

    private final SeatLockManager seatLockManager;

    private final SeatBroadcastService seatBroadcastService;

    public PaymentService(
            PaymentRepository paymentRepository,
            BookingRepository bookingRepository,
            BookingSeatRepository bookingSeatRepository,
            RazorpayService razorpayService,
            SeatLockManager seatLockManager,
            SeatBroadcastService seatBroadcastService) {

        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.bookingSeatRepository = bookingSeatRepository;
        this.razorpayService = razorpayService;
        this.seatLockManager = seatLockManager;
        this.seatBroadcastService = seatBroadcastService;
    }

    /*
     * Process payment — on success mark BOOKED; on failure restore AVAILABLE.
     * Seat status changes go through SeatLockManager; WS updates via SeatBroadcastService.
     */
    public PaymentResponse processPayment(PaymentRequest request) {

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        boolean paymentSuccess = razorpayService.processPayment(request.getAmount());

        Payment payment = new Payment();
        payment.setAmount(request.getAmount());
        payment.setBooking(booking);
        payment.setTransactionId(razorpayService.createTransactionId());
        payment.setPaymentTime(LocalDateTime.now());

        List<BookingSeat> bookingSeats = bookingSeatRepository.findByBookingId(booking.getId());

        if (paymentSuccess) {

            payment.setStatus(PaymentStatus.SUCCESS);
            booking.setStatus(BookingStatus.CONFIRMED);

            for (BookingSeat bookingSeat : bookingSeats) {

                Seat seat = bookingSeat.getSeat();

                /*
                 * Unlock from Redis and set seat status to BOOKED in DB.
                 */
                
                seatLockManager.confirmSeat(booking.getShow().getId(), seat.getId());
                
                
                /*
                 * FLOW AFTER PAYMENT SUCCESS:
                 *
                 * PAYMENT SUCCESS
                 *        ↓
                 * SeatLockManager confirms seat
                 *        ↓
                 * Seat status becomes BOOKED
                 *        ↓
                 * WebSocket message broadcast
                 *        ↓
                 * All users instantly see:
                 * "Seat Booked"
                 */
                
                broadcastSeatStatus(booking.getShow().getId(), seat.getId(), "BOOKED");
            }

        } else {

            payment.setStatus(PaymentStatus.FAILED);
            booking.setStatus(BookingStatus.FAILED);

            for (BookingSeat bookingSeat : bookingSeats) {

                Seat seat = bookingSeat.getSeat();

                seatLockManager.unlockSeat(booking.getShow().getId(), seat.getId());

                broadcastSeatStatus(booking.getShow().getId(), seat.getId(), "AVAILABLE");
            }
        }

        bookingRepository.save(booking);

        Payment savedPayment = paymentRepository.save(payment);

        return mapToResponse(savedPayment);
    }

    /*
     * Get payment by ID
     */
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        return mapToResponse(payment);
    }

    /*
     * Helper: build and broadcast a seat status update.
     */
    private void broadcastSeatStatus(Long showId, Long seatId, String status) {

        SeatUpdateMessage msg = new SeatUpdateMessage();
        msg.setShowId(showId);
        msg.setSeatId(seatId);
        msg.setStatus(status);
        seatBroadcastService.broadcastSeatUpdate(msg);
    }

    /*
     * Entity -> Response
     */
    private PaymentResponse mapToResponse(Payment payment) {

        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(payment.getId());
        response.setBookingId(payment.getBooking().getId());
        response.setTransactionId(payment.getTransactionId());
        response.setAmount(payment.getAmount());
        response.setPaymentStatus(payment.getStatus());
        response.setPaymentTime(payment.getPaymentTime());
        return response;
    }
}
