package com.kgstrivers.trainticker.Entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "coach_type_config")
@Data
public class CoachTypeConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String coachType;

    // Total confirmed seats
    private Integer confirmedCapacity;

    // RAC seats
    private Integer racCapacity;

    // Waiting list limit
    private Integer waitingLimit;

    // Tatkal quota
    private Integer tatkalCapacity;

    // Base fare per km
    private Double farePerKm;
}