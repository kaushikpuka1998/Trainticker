package com.kgstrivers.trainticker.Services;

import com.kgstrivers.trainticker.Entities.Coach;
import com.kgstrivers.trainticker.Entities.Seat;
import com.kgstrivers.trainticker.Entities.Station;
import com.kgstrivers.trainticker.Entities.Train;
import com.kgstrivers.trainticker.Repositories.StationRepository;
import com.kgstrivers.trainticker.Repositories.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TrainService {
    @Autowired
    TrainRepository trainRepository;

    @Autowired
    StationRepository stationRepository;

    public TrainService(TrainRepository trainRepository) {
        this.trainRepository = trainRepository;
    }


    public List<Train> getAllTrain() {
        return trainRepository.findAll();
    }

    public Train addTrain(Train train) {
        Train updatedTrain = findDuplicateStation(train);
        return trainRepository.save(updatedTrain);
    }

    private Train findDuplicateStation(Train train) {
        train.getRouteStations().forEach(route -> {

            Station existingStation =
                    stationRepository
                            .findByCode(route.getCode())
                            .orElse(null);

            if (existingStation != null) {

                route.setStation(existingStation);

            } else {

                route.mapStation();
            }

            route.setTrain(train);
        });

        return train;
    }


}
