package com.kgstrivers.trainticker.Repositories;

import com.kgstrivers.trainticker.Entities.BookedSeat;
import com.kgstrivers.trainticker.Enums.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookedSeatRepository extends JpaRepository<BookedSeat, Long> {
    List<BookedSeat> findByBooking_Train_IdAndBooking_JourneyDateAndSeatStatusOrderByIdAsc(Long trainId, LocalDate journeyDate, SeatStatus seatStatus );
}
