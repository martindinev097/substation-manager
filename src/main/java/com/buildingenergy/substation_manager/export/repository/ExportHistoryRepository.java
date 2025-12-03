package com.buildingenergy.substation_manager.export.repository;

import com.buildingenergy.substation_manager.export.model.ExportHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExportHistoryRepository extends JpaRepository<ExportHistory, UUID> {

    List<ExportHistory> findAllByUserIdOrderByExportedAtDesc(UUID userId);

}
