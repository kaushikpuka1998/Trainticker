package com.kgstrivers.trainticker.Entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "route_stations")
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class RouteStation {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "train_id")
    private Train train;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station station;

    private int stationOrder;
    private String arrivalTime;
    private String departureTime;
}
