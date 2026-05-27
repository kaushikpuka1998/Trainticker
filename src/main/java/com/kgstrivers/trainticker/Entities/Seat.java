package com.kgstrivers.trainticker.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "seats")
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class Seat {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private int id;
    private String seatNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coach_id")
    private Coach coach;

    private long occupiedMask;
    @Version
    private Long version;

    public boolean isAvailable(long requestMask) {
        return (occupiedMask & requestMask) == 0;
    }

    public void reserve(long requestMask) {
        occupiedMask |= requestMask;
    }

    public void release(long requestMask) {
        occupiedMask &= ~requestMask;
    }

}
