package com.kgstrivers.trainticker.Repositories;

import com.kgstrivers.trainticker.Entities.Coach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoachRepository extends JpaRepository<Coach, Long> {
}
