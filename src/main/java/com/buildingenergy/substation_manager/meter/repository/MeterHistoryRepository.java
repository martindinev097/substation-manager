package com.buildingenergy.substation_manager.meter.repository;

import com.buildingenergy.substation_manager.meter.model.MeterHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MeterHistoryRepository extends JpaRepository<MeterHistory, UUID> {

    List<MeterHistory> findAllByUserIdSnapshotOrderBySavedAtDesc(UUID userIdSnapshot);

    @Modifying
    @Query("DELETE FROM meter_history m WHERE m.meterIdSnapshot = :meterIdSnapshot AND MONTH(m.savedAt) = :savedAtMonthValue")
    void deleteByMeterIdSnapshotAndSavedAt_MonthValue(UUID meterIdSnapshot, int savedAtMonthValue);
}
