package com.booking.payment;

import com.booking.exception.SeatLockException;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RazorpayService {

    public String createTransactionId() {

        return "TXN-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 16)
                .toUpperCase();
    }

    public boolean processPayment(Double amount) {

        validateAmount(amount);


        return true;
    }

    public boolean refundPayment(
            String transactionId) {

        if (transactionId == null
                || transactionId.isBlank()) {
            throw new SeatLockException("Invalid transaction ID");
        }


        return true;
    }

    private void validateAmount(
            Double amount) {

        if (amount == null || amount <= 0) {

            throw new SeatLockException("Invalid payment amount");
        }
    }
}