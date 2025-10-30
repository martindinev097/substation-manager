package com.buildingenergy.substation_manager.user.repository;

import com.buildingenergy.substation_manager.user.model.Reading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReadingRepository extends JpaRepository<Reading, UUID> {
}
