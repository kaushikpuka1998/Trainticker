package com.kgstrivers.trainticker.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "coaches")
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class Coach {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String coachNumber;
    private String coachType;

    @ManyToOne
    @JoinColumn(name="train_id")
    @JsonBackReference
    private Train train;

    @OneToMany(mappedBy = "coach", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Seat> seats;

    @PrePersist
    public void generateSeats() {

        if (seats == null || seats.isEmpty()) {

            seats = new ArrayList<>();

            int totalSeats = switch (coachType) {
                case "1A" -> 24;
                case "2A" -> 54;
                case "3A" -> 72;
                case "SL" -> 80;
                default -> 50;
            };

            for (int i = 1; i <= totalSeats; i++) {

                Seat seat = new Seat();

                seat.setSeatNumber(String.valueOf(i));

                seat.setCoach(this);

                seats.add(seat);
            }
        }
    }
}
