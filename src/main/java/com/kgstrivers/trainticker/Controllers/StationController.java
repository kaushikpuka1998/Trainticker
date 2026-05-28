package com.kgstrivers.trainticker.Controllers;

import com.kgstrivers.trainticker.Entities.Station;
import com.kgstrivers.trainticker.Services.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/stations")
public class StationController {


    @Autowired
    StationService stationService;
    @PostMapping("/")
    public ResponseEntity<Station> addStation(@RequestBody Station station) {
        return ResponseEntity.ok(stationService.addStation(station));
    }
}
