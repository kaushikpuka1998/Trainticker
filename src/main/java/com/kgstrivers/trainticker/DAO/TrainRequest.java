package com.kgstrivers.trainticker.DAO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TrainRequest {

    private String trainNumber;

    private String trainName;

    private List<RouteStationRequest> routeStations;
}
