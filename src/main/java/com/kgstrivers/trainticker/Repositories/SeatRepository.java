package com.kgstrivers.trainticker.Repositories;

import com.kgstrivers.trainticker.Entities.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat>
    findByCoach_Train_IdAndCoach_CoachType(
            Long trainId,
            String coachType
    );
}
