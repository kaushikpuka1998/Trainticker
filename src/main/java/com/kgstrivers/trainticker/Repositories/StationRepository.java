package com.kgstrivers.trainticker.Repositories;

import com.kgstrivers.trainticker.Entities.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {
    Optional<Station> findByCode(String code);
}
