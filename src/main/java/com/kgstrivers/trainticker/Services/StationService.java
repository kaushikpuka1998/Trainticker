package com.kgstrivers.trainticker.Services;

import com.kgstrivers.trainticker.Entities.Station;
import com.kgstrivers.trainticker.Repositories.StationRepository;
import com.kgstrivers.trainticker.Repositories.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StationService {

    @Autowired
    StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Station addStation(Station rs) {

        return stationRepository
                .findByCode(rs.getCode())
                .orElseGet(() -> {

                    Station newStation = new Station();

                    newStation.setCode(rs.getCode());
                    newStation.setName(rs.getName());

                    return stationRepository.save(newStation);
                });
    }
}
