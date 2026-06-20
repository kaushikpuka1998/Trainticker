package com.kgstrivers.trainticker.Services;

import com.kgstrivers.trainticker.Entities.CoachTypeConfig;
import com.kgstrivers.trainticker.Repositories.CoachTypeConfigRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoachTypeConfigService {
    private final CoachTypeConfigRepository coachTypeConfigRepository;

    @Transactional
    public List<CoachTypeConfig> saveAllConfigs(List<CoachTypeConfig> configs) {
        List<CoachTypeConfig> toSave = configs.stream()
                .map(incoming -> {
                    coachTypeConfigRepository
                            .findByCoachType(incoming.getCoachType())
                            .ifPresent(existing -> incoming.setId(existing.getId()));
                    return incoming;
                })
                .toList();
        return coachTypeConfigRepository.saveAll(toSave);
    }

    public List<CoachTypeConfig> getAllConfigs() {
        return coachTypeConfigRepository.findAll();
    }

    public CoachTypeConfig getByCoachType(String coachType) {
        return coachTypeConfigRepository.findByCoachType(coachType).orElseThrow(() -> new RuntimeException("Coach type not found"));
    }
}
