package com.kgstrivers.trainticker.Entities;


import com.kgstrivers.trainticker.Entities.RouteStation;

public record RouteStationDTO(
        Long id,
        Long stationId,
        String code,
        String name,
        String state,
        int stationOrder,
        String arrivalTime,
        Long distanceFromSource,
        String departureTime,
        Long dayNumber
) {
    public static RouteStationDTO from(RouteStation rs) {
        return new RouteStationDTO(
                rs.getId(),
                rs.getStation() != null ? rs.getStation().getId() : null,
                rs.getStation() != null ? rs.getStation().getCode() : null,
                rs.getStation() != null ? rs.getStation().getName() : null,
                rs.getStation() != null ? rs.getStation().getState() : null,
                rs.getStationOrder(),
                rs.getArrivalTime(),
                rs.getDistanceFromSource(),
                rs.getDepartureTime(),
                rs.getDayNumber()
        );
    }
}