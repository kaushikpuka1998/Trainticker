package com.kgstrivers.trainticker.DAO;

import com.kgstrivers.trainticker.Entities.BerthType;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassengerSeatResponse {
    private Long id;
    private String passengerName;
    private String coachNumber;
    private String seatNumber;
    private String bookingStatus;
    @Nullable
    private BerthType berthType;
}
