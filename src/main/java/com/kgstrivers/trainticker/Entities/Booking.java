package com.kgstrivers.trainticker.Entities;


import com.kgstrivers.trainticker.Enums.BookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "bookings")
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String pnr;

    private LocalDate journeyDate;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="train_id")
    private Train train;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="source_station_id")
    private Station sourceStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="destination_station_id")
    private Station destinationStation;

    private Long journeyMask;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<BookedSeat> bookedSeats;
}
