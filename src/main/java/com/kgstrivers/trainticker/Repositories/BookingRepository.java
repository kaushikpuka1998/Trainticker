package com.kgstrivers.trainticker.Repositories;

import com.kgstrivers.trainticker.Entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByPnr(String pnr);
    List<Booking>
    findByTrain_IdAndJourneyDate(
            Long trainId,
            LocalDate journeyDate
    );
}
