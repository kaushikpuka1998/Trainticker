package com.kgstrivers.trainticker.Controllers;


import com.kgstrivers.trainticker.Entities.CoachTypeConfig;
import com.kgstrivers.trainticker.Services.CoachTypeConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/coach-configs")
@RequiredArgsConstructor
public class CoachConfigController {
    private final CoachTypeConfigService coachTypeConfigService;

    @PostMapping
    public ResponseEntity<List<CoachTypeConfig>> createConfigs(@RequestBody List<CoachTypeConfig> configs) {
        return ResponseEntity.ok(coachTypeConfigService.saveAllConfigs(configs));
    }

    @GetMapping
    public ResponseEntity<List<CoachTypeConfig>> getAllConfigs() {
        return ResponseEntity.ok(coachTypeConfigService.getAllConfigs());
    }

    @GetMapping("/{coachType}")
    public ResponseEntity<CoachTypeConfig> getByCoachType(@PathVariable String coachType) {
        return ResponseEntity.ok(coachTypeConfigService.getByCoachType(coachType));
    }
}
