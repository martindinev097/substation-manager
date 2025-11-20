package com.buildingenergy.substation_manager.reading.repository;

import com.buildingenergy.substation_manager.reading.model.ReadingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReadingHistoryRepository extends JpaRepository<ReadingHistory, UUID> {

    List<ReadingHistory> findAllByUserIdSnapshotOrderBySavedAtDesc(UUID userIdSnapshot);

    @Modifying
    @Query("DELETE FROM reading_history r WHERE r.companyIdSnapshot = :companyIdSnapshot AND MONTH(r.savedAt) = :savedAtMonthValue")
    void deleteByCompanyIdSnapshotAndMonthValue(UUID companyIdSnapshot, int savedAtMonthValue);
}
