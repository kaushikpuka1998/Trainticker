package com.kgstrivers.trainticker.DAO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PassengerSeatResponse {
    private Long id;
    private String passengerName;
    private String coachNumber;
    private String seatNumber;
    private String bookingStatus;
}
