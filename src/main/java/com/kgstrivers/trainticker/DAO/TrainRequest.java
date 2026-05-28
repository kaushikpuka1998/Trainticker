package com.kgstrivers.trainticker.DAO;

import lombok.Data;

import java.util.List;

@Data
public class TrainRequest {

    private String trainNumber;

    private String trainName;

    private List<RouteStationRequest> routeStations;
}
