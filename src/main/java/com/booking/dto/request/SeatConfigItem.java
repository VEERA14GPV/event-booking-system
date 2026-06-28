package com.booking.dto.request;

public class SeatConfigItem {

    private String seatNumber;
    private String seatType;

    public SeatConfigItem() {}

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public String getSeatType() { return seatType; }
    public void setSeatType(String seatType) { this.seatType = seatType; }
}
