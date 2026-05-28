package com.kgstrivers.trainticker.Repositories;

import com.kgstrivers.trainticker.Entities.RouteStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteStationRepository extends JpaRepository<RouteStation, Long> {
}
