package com.buildingenergy.substation_manager.meter.repository;

import com.buildingenergy.substation_manager.meter.model.MeterHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MeterHistoryRepository extends JpaRepository<MeterHistory, UUID> {

    List<MeterHistory> findAllByOrderBySavedAtDesc();

}
