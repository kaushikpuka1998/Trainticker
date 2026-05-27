package com.kgstrivers.trainticker.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Entity
@Table(name = "coaches")
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class Coach {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private int id;
    private String coachNumber;
    private String CoachType;

    @ManyToOne
    @JoinColumn(name="train_id")
    private Train train;

    @OneToMany(mappedBy = "coach", cascade = CascadeType.ALL)
    private List<Seat> seats;
}
