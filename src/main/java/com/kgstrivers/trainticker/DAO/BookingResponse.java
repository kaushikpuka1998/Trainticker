package com.kgstrivers.trainticker.DAO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class BookingResponse {
    private String pnr;
    private String trainNumber;
    private String trainName;
    private LocalDate journeyDate;
    private String source;
    private String destination;
    private String bookingStatus;
    private List<PassengerSeatResponse> passengers;
}
