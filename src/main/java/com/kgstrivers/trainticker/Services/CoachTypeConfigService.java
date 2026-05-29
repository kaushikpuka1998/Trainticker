package com.kgstrivers.trainticker.Services;

import com.kgstrivers.trainticker.Entities.CoachTypeConfig;
import com.kgstrivers.trainticker.Repositories.CoachTypeConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoachTypeConfigService {
    private final CoachTypeConfigRepository coachTypeConfigRepository;

    public List<CoachTypeConfig> saveAllConfigs(List<CoachTypeConfig> configs) {
        return coachTypeConfigRepository.saveAll(configs);
    }

    public List<CoachTypeConfig> getAllConfigs() {
        return coachTypeConfigRepository.findAll();
    }

    public CoachTypeConfig getByCoachType(String coachType) {
        return coachTypeConfigRepository.findByCoachType(coachType).orElseThrow(() -> new RuntimeException("Coach type not found"));
    }
}
