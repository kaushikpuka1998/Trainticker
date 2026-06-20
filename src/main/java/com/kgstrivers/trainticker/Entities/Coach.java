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
                case "PANTRY" -> 0;
                default -> 50;
            };

            for (int i = 1; i <= totalSeats; i++) {
                Seat seat = new Seat();
                seat.setSeatNumber(String.valueOf(i));
                seat.setBerthType(resolveBerth(coachType, i));
                seat.setCoach(this);
                seats.add(seat);
            }
        }
    }

    private BerthType resolveBerth(String type, int seatNo) {
        return switch (type) {
            case "SL", "3A" -> {
                int pos = ((seatNo - 1) % 8) + 1; // 1..8 per compartment
                yield switch (pos) {
                    case 1, 4 -> BerthType.LOWER;
                    case 2, 5 -> BerthType.MIDDLE;
                    case 3, 6 -> BerthType.UPPER;
                    case 7    -> BerthType.SIDE_LOWER;
                    case 8    -> BerthType.SIDE_UPPER;
                    default   -> BerthType.NONE;
                };
            }
            case "2A" -> {
                int pos = ((seatNo - 1) % 6) + 1; // 2A has no middle: L,U,L,U,SL,SU
                yield switch (pos) {
                    case 1, 3 -> BerthType.LOWER;
                    case 2, 4 -> BerthType.UPPER;
                    case 5    -> BerthType.SIDE_LOWER;
                    case 6    -> BerthType.SIDE_UPPER;
                    default   -> BerthType.NONE;
                };
            }
            case "1A" -> ((seatNo % 2) == 1) ? BerthType.LOWER : BerthType.UPPER;
            default   -> BerthType.NONE;
        };
    }
}
