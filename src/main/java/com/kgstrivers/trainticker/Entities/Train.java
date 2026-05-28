package com.kgstrivers.trainticker.Entities;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trains")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Train {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trainNumber;

    private String trainName;

    @OneToMany(mappedBy = "train",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonManagedReference
    private List<RouteStation> routeStations = new ArrayList<>();

    @OneToMany(mappedBy = "train",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonManagedReference
    private List<Coach> coaches = new ArrayList<>();

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<TrainSchedule> schedules;


    @PrePersist
    @PreUpdate
    public void setRelations() {

        if (coaches != null) {
            coaches.forEach(coach -> coach.setTrain(this));
        }

        if (routeStations != null) {
            routeStations.forEach(route -> route.setTrain(this));
        }

        if (schedules != null) {
            schedules.forEach(
                    schedule -> schedule.setTrain(this)
            );
        }
    }
}
