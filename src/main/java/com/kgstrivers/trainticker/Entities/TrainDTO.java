package com.kgstrivers.trainticker.Entities;

import java.util.List;

public class TrainDTO {

    private final Long id;
    private final String trainNumber;
    private final String trainName;
    private final List<RouteStationDTO> routeStations;
    private final List<CoachDTO> coaches;
    private final List<TrainScheduleDTO> schedules;

    public TrainDTO(Long id, String trainNumber, String trainName,
                    List<RouteStationDTO> routeStations,
                    List<CoachDTO> coaches,
                    List<TrainScheduleDTO> schedules) {
        this.id = id;
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.routeStations = routeStations;
        this.coaches = coaches;
        this.schedules = schedules;
    }

    public Long getId() { return id; }
    public String getTrainNumber() { return trainNumber; }
    public String getTrainName() { return trainName; }
    public List<RouteStationDTO> getRouteStations() { return routeStations; }
    public List<CoachDTO> getCoaches() { return coaches; }
    public List<TrainScheduleDTO> getSchedules() { return schedules; }

    public static TrainDTO from(Train t) {
        return new TrainDTO(
                t.getId(),
                t.getTrainNumber(),
                t.getTrainName(),
                t.getRouteStations() != null
                        ? t.getRouteStations().stream().map(RouteStationDTO::from).toList()
                        : List.of(),
                t.getCoaches() != null
                        ? t.getCoaches().stream().map(CoachDTO::from).toList()
                        : List.of(),
                t.getSchedules() != null
                        ? t.getSchedules().stream().map(TrainScheduleDTO::from).toList()
                        : List.of()
        );
    }
}