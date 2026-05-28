package com.kgstrivers.trainticker.Repositories;

import com.kgstrivers.trainticker.Entities.RouteStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RouteStationRepository extends JpaRepository<RouteStation, Long> {

    Optional<RouteStation>
    findByTrain_IdAndStation_Code(
            Long trainId,
            String code
    );

}
