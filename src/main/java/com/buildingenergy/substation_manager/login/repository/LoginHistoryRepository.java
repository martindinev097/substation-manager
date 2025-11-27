package com.buildingenergy.substation_manager.login.repository;

import com.buildingenergy.substation_manager.login.model.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, UUID> {

    long deleteByLoginTimeBefore(LocalDateTime loginTimeBefore);

}
