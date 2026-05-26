package com.booking.service;

import com.booking.dto.request.PaymentRequest;

import com.booking.dto.response.PaymentResponse;

import com.booking.entity.Booking;
import com.booking.entity.BookingSeat;
import com.booking.entity.Payment;
import com.booking.entity.Seat;

import com.booking.enums.BookingStatus;
import com.booking.enums.PaymentStatus;
import com.booking.enums.SeatStatus;

import com.booking.exception.ResourceNotFoundException;

import com.booking.payment.RazorpayService;

import com.booking.repository.BookingRepository;
import com.booking.repository.BookingSeatRepository;
import com.booking.repository.PaymentRepository;
import com.booking.repository.SeatRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final BookingRepository bookingRepository;

    private final BookingSeatRepository bookingSeatRepository;

    private final SeatRepository seatRepository;

    private final RazorpayService razorpayService;

    private final SeatLockService seatLockService;

    public PaymentService(
            PaymentRepository paymentRepository,
            BookingRepository bookingRepository,
            BookingSeatRepository bookingSeatRepository,
            SeatRepository seatRepository,
            RazorpayService razorpayService,
            SeatLockService seatLockService) {

        this.paymentRepository =
                paymentRepository;

        this.bookingRepository =
                bookingRepository;

        this.bookingSeatRepository =
                bookingSeatRepository;

        this.seatRepository =
                seatRepository;

        this.razorpayService =
                razorpayService;

        this.seatLockService =
                seatLockService;
    }

    /*
     * Process payment
     */
    public PaymentResponse processPayment(
            PaymentRequest request) {

        Booking booking =
                bookingRepository.findById(
                        request.getBookingId()
                )
                .orElseThrow(() ->

                        new ResourceNotFoundException(
                                "Booking not found"
                        )
                );

        boolean paymentSuccess =
                razorpayService.processPayment(
                        request.getAmount()
                );

        Payment payment = new Payment();

        payment.setAmount(
                request.getAmount()
        );

        payment.setBooking(
                booking
        );

        payment.setTransactionId(
                razorpayService
                        .createTransactionId()
        );

        payment.setPaymentTime(
                LocalDateTime.now()
        );

        List<BookingSeat> bookingSeats =
                bookingSeatRepository
                        .findByBookingId(
                                booking.getId()
                        );

        if (paymentSuccess) {

            payment.setStatus(
                    PaymentStatus.SUCCESS
            );

            booking.setStatus(
                    BookingStatus.CONFIRMED
            );

            for (BookingSeat bookingSeat :
                    bookingSeats) {

                Seat seat =
                        bookingSeat.getSeat();

                seat.setStatus(
                        SeatStatus.BOOKED
                );

                seatRepository.save(seat);

                seatLockService.unlockSeat(
                        booking.getShow().getId(),
                        seat.getId()
                );
            }

        } else {

            payment.setStatus(
                    PaymentStatus.FAILED
            );

            booking.setStatus(
                    BookingStatus.FAILED
            );

            for (BookingSeat bookingSeat :
                    bookingSeats) {

                Seat seat =
                        bookingSeat.getSeat();

                seat.setStatus(
                        SeatStatus.AVAILABLE
                );

                seatRepository.save(seat);

                seatLockService.unlockSeat(
                        booking.getShow().getId(),
                        seat.getId()
                );
            }
        }

        bookingRepository.save(
                booking
        );

        Payment savedPayment =
                paymentRepository.save(
                        payment
                );

        return mapToResponse(
                savedPayment
        );
    }

    /*
     * Get payment by ID
     */
    public PaymentResponse getPaymentById(
            Long paymentId) {

        Payment payment =
                paymentRepository.findById(
                        paymentId
                )
                .orElseThrow(() ->

                        new ResourceNotFoundException(
                                "Payment not found"
                        )
                );

        return mapToResponse(
                payment
        );
    }

    /*
     * Entity -> Response
     */
    private PaymentResponse mapToResponse(
            Payment payment) {

        PaymentResponse response =
                new PaymentResponse();

        response.setPaymentId(
                payment.getId()
        );

        response.setBookingId(
                payment.getBooking()
                        .getId()
        );

        response.setTransactionId(
                payment.getTransactionId()
        );

        response.setAmount(
                payment.getAmount()
        );

        response.setPaymentStatus(
                payment.getStatus()
        );

        response.setPaymentTime(
                payment.getPaymentTime()
        );

        return response;
    }
}