package com.kgstrivers.trainticker.Entities;

import com.kgstrivers.trainticker.Enums.SeatStatus;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "booked_seats")
@Data
public class BookedSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "seat_id")
    private Seat seat;

    private String passengerName;

    private Integer passengerAge;

    private String gender;

    @Enumerated(EnumType.STRING)
    private SeatStatus seatStatus;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;

    private Integer racNumber;
    private Integer waitingNumber;
}
