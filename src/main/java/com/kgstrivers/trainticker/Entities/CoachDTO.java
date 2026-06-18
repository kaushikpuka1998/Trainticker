package com.kgstrivers.trainticker.Entities;

import com.kgstrivers.trainticker.Entities.Coach;

import java.util.List;

public record CoachDTO(
        Long id,
        String coachNumber,
        String coachType,
        List<SeatDTO> seats
) {
    public static CoachDTO from(Coach c) {
        return new CoachDTO(
                c.getId(),
                c.getCoachNumber(),
                c.getCoachType(),
                c.getSeats() != null
                        ? c.getSeats().stream().map(SeatDTO::from).toList()
                        : List.of()
        );
    }
}
