package com.kgstrivers.trainticker.DAO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RouteStationRequest {

    private String stationCode;

    private String stationName;

    private String arrivalTime;

    private String departureTime;

    private Integer dayNumber;

    private Integer distanceFromSource;

    private Integer stationOrder;
}