package com.kgstrivers.trainticker.Entities;

public record SeatDTO(
        int id,
        String seatNumber
        // add remaining Seat fields
) {
    public static SeatDTO from(Seat seat) {
        return new SeatDTO(seat.getId(), seat.getSeatNumber());
    }
}
