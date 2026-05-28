package com.kgstrivers.trainticker.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "train_schedules")
@Data
public class TrainSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "train_id")
    @JsonBackReference
    private Train train;

    private LocalDate startDate;
    private LocalDate endDate;
    private String runningDays;

    // MTWTFSS
    private Boolean monday;
    private Boolean tuesday;
    private Boolean wednesday;
    private Boolean thursday;
    private Boolean friday;
    private Boolean saturday;
    private Boolean sunday;

    private Boolean active;


    @PreUpdate
    @PrePersist
    public void updateRunningDays() {
        if (runningDays != null) {
            setMonday(runningDays.charAt(0) == '1');
            setTuesday(runningDays.charAt(1) == '1');
            setWednesday(runningDays.charAt(2) == '1');
            setThursday(runningDays.charAt(3) == '1');
            setFriday(runningDays.charAt(4) == '1');
            setSaturday(runningDays.charAt(5) == '1');
            setSunday(runningDays.charAt(6) == '1');
        }
    }
}
