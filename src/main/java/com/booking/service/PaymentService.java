package com.booking.service;

import com.booking.dto.request.PaymentRequest;
import com.booking.dto.response.PaymentResponse;

import com.booking.entity.Booking;
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

    public PaymentService(
            PaymentRepository paymentRepository,
            BookingRepository bookingRepository,
            BookingSeatRepository bookingSeatRepository,
            SeatRepository seatRepository,
            RazorpayService razorpayService) {

        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.bookingSeatRepository = bookingSeatRepository;
        this.seatRepository = seatRepository;
        this.razorpayService = razorpayService;
    }

    public PaymentResponse processPayment(
            PaymentRequest request) {

        Booking booking =
                bookingRepository.findById(
                        request.getBookingId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found"
                        )
                );

        boolean paymentSuccess =
                razorpayService.processPayment(
                        request.getAmount()
                );

        Payment payment = new Payment();

        payment.setAmount(request.getAmount());

        payment.setTransactionId(
                razorpayService.createTransactionId()
        );

        payment.setBooking(booking);

        payment.setPaymentTime(LocalDateTime.now());

        if (paymentSuccess) {

            payment.setStatus(PaymentStatus.SUCCESS);

            booking.setStatus(
                    BookingStatus.CONFIRMED
            );

            List<Seat> seats =
                    bookingSeatRepository.findAll()
                            .stream()
                            .filter(bs ->
                                    bs.getBooking()
                                            .getId()
                                            .equals(booking.getId())
                            )
                            .map(bs -> bs.getSeat())
                            .toList();

            for (Seat seat : seats) {

                seat.setStatus(SeatStatus.BOOKED);

                seatRepository.save(seat);
            }

        } else {

            payment.setStatus(PaymentStatus.FAILED);

            booking.setStatus(
                    BookingStatus.FAILED
            );
        }

        bookingRepository.save(booking);

        Payment savedPayment =
                paymentRepository.save(payment);

        return new PaymentResponse(
                savedPayment.getId(),
                savedPayment.getTransactionId(),
                savedPayment.getAmount(),
                savedPayment.getStatus().name()
        );
    }
}