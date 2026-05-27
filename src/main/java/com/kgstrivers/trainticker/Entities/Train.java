package com.kgstrivers.trainticker.Entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Entity
@Table(name = "trains")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Train {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String trainNumber;
    private String trainName;

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL)
    private List<RouteStation> routeStations;
    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL)
    private List<Coach> coaches;
}
