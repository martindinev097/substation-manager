package com.buildingenergy.substation_manager.reading.repository;

import com.buildingenergy.substation_manager.reading.model.ReadingHistory;
import com.buildingenergy.substation_manager.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReadingHistoryRepository extends JpaRepository<ReadingHistory, UUID> {

    List<ReadingHistory> findAllByCompany_UserOrderBySavedAtDesc(User user);

}
