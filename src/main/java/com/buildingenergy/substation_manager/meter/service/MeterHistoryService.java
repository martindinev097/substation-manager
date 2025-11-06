package com.buildingenergy.substation_manager.meter.service;

import com.buildingenergy.substation_manager.meter.model.Meter;
import com.buildingenergy.substation_manager.meter.model.MeterHistory;
import com.buildingenergy.substation_manager.meter.repository.MeterHistoryRepository;
import com.buildingenergy.substation_manager.user.model.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MeterHistoryService {

    private final MeterHistoryRepository meterHistoryRepository;

    public MeterHistoryService(MeterHistoryRepository meterHistoryRepository) {
        this.meterHistoryRepository = meterHistoryRepository;
    }

    public void backupCurrentReadings(List<Meter> meters, User user) {
        List<MeterHistory> metersHistory = meters.stream()
                .map(m -> MeterHistory.builder()
                        .meter(m)
                        .user(user)
                        .meterName(m.getMeterName())
                        .room(m.getRoom())
                        .description(m.getDescription())
                        .outsideBody(m.getOutsideBody())
                        .energyPercentage(m.getEnergyPercentage())
                        .oldReadings(m.getOldReadings())
                        .newReadings(m.getNewReadings())
                        .differenceReadings(m.getDifferenceReadings())
                        .totalCost(m.getTotalCost())
                        .savedAt(LocalDateTime.now())
                        .build()).toList();

        meterHistoryRepository.saveAll(metersHistory);
    }

    public List<MeterHistory> getAll() {
        return meterHistoryRepository.findAllByOrderBySavedAtDesc().stream().filter(m -> m.getNewReadings().compareTo(BigDecimal.ZERO) != 0).toList();
    }
}
