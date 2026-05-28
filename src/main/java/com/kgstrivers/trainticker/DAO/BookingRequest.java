package com.kgstrivers.trainticker.DAO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    private String trainNumber;
    private LocalDate journeyDate;
    private String sourceStationCode;
    private String destinationStationCode;
    private String classType;
    private List<PassengerRequest> passengers;
}
