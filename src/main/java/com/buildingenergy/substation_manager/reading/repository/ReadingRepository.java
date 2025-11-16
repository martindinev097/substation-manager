package com.buildingenergy.substation_manager.reading.repository;

import com.buildingenergy.substation_manager.company.model.Company;
import com.buildingenergy.substation_manager.floor.model.Floor;
import com.buildingenergy.substation_manager.reading.model.Reading;
import com.buildingenergy.substation_manager.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReadingRepository extends JpaRepository<Reading, UUID> {

    Optional<Reading> findByCompany(Company company);

    List<Reading> findAllByCompany_UserAndCompany_FloorAndNewReadingM1AndNewReadingM2(User user, Floor floor, BigDecimal newReadingM1, BigDecimal newReadingM2);

    List<Reading> findAllByCompany_UserAndCompany_Floor(User user, Floor floor);
}
