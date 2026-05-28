package com.kgstrivers.trainticker.Entities;


import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @JsonBackReference
    private Train train;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "station_id")
    private Station station;

    private int stationOrder;
    private String arrivalTime;
    private Long distanceFromSource;
    private String departureTime;
    private Long dayNumber;

    @Transient
    private String code;

    @Transient
    private String name;

    @Transient
    private String state;

    @PrePersist
    @PreUpdate
    public void mapStation() {

        if (station == null && code != null) {

                Station s = new Station();

                s.setCode(code);
                s.setName(name);
                s.setState(state);

                this.station = s;
            }
        }
    }
