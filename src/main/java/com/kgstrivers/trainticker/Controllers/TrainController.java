package com.kgstrivers.trainticker.Controllers;


import com.kgstrivers.trainticker.Entities.Train;
import com.kgstrivers.trainticker.Entities.TrainDTO;
import com.kgstrivers.trainticker.Services.TrainService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TrainController {

    @Autowired
    TrainService trainService;

    @GetMapping("/trains")
    public ResponseEntity<List<Train>> getAllTrains(){
        return ResponseEntity.ok(trainService.getAllTrain());
    }

    @GetMapping("/trains/{trainNumber}")
    public ResponseEntity<TrainDTO> getTrainDetails(@PathVariable String trainNumber){
        return ResponseEntity.ok(trainService.getTrain(trainNumber));
    }

    @PostMapping("/trains")
    public ResponseEntity<Train> addTrain(@RequestBody Train train) {
        return ResponseEntity.ok(trainService.addTrain(train));
    }
}
