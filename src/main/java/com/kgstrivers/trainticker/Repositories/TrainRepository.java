package com.kgstrivers.trainticker.Repositories;

import com.kgstrivers.trainticker.Entities.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainRepository extends JpaRepository<Train, Long> {

}
