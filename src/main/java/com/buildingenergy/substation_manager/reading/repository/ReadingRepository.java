package com.buildingenergy.substation_manager.reading.repository;

import com.buildingenergy.substation_manager.company.model.Company;
import com.buildingenergy.substation_manager.reading.model.Reading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReadingRepository extends JpaRepository<Reading, UUID> {

    Optional<Reading> findByCompany(Company company);

}
