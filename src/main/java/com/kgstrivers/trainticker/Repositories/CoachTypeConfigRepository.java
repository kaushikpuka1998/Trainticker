package com.kgstrivers.trainticker.Repositories;

import com.kgstrivers.trainticker.Entities.CoachTypeConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CoachTypeConfigRepository extends JpaRepository<CoachTypeConfig, Long> {
    Optional<CoachTypeConfig> findByCoachType(String coachType);
}
