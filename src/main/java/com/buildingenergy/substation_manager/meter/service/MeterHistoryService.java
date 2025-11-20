package com.buildingenergy.substation_manager.meter.service;

import com.buildingenergy.substation_manager.meter.model.Meter;
import com.buildingenergy.substation_manager.meter.model.MeterHistory;
import com.buildingenergy.substation_manager.meter.repository.MeterHistoryRepository;
import com.buildingenergy.substation_manager.user.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class MeterHistoryService {

    private final MeterHistoryRepository meterHistoryRepository;

    public MeterHistoryService(MeterHistoryRepository meterHistoryRepository) {
        this.meterHistoryRepository = meterHistoryRepository;
    }

    public void backupCurrentReadings(List<Meter> meters, User user) {
        List<MeterHistory> metersHistory = meters.stream()
                .map(m -> MeterHistory.builder()
                        .meterIdSnapshot(m.getId())
                        .userIdSnapshot(user.getId())
                        .meterNameSnapshot(m.getMeterName())
                        .room(m.getRoom())
                        .description(m.getDescription())
                        .outsideBody(m.getOutsideBody())
                        .energyPercentage(m.getEnergyPercentage())
                        .oldReadings(m.getOldReadings())
                        .newReadings(m.getNewReadings())
                        .differenceReadings(m.getDifferenceReadings())
                        .totalCost(m.getTotalCost())
                        .savedAt(m.getCreatedOn())
                        .build()).toList();

        meterHistoryRepository.saveAll(metersHistory);
    }

    public List<MeterHistory> getAllByMonthAndUser(Integer month, User user) {
        return meterHistoryRepository.findAllByUserIdSnapshotOrderBySavedAtDesc(user.getId()).stream()
                .filter(m -> m.getNewReadings().compareTo(BigDecimal.ZERO) != 0)
                .filter(m -> m.getSavedAt().getMonthValue() == month)
                .toList();
    }

    @Transactional
    public void deleteMeterByIdAndMonth(UUID meterId, int month) {
        meterHistoryRepository.deleteByMeterIdSnapshotAndSavedAt_MonthValue(meterId, month);
    }
}
