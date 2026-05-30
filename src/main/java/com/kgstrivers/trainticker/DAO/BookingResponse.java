package com.kgstrivers.trainticker.DAO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
