package com.buildingenergy.substation_manager.user.repository;

import com.buildingenergy.substation_manager.user.model.Company;
import com.buildingenergy.substation_manager.user.model.Floor;
import com.buildingenergy.substation_manager.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {

    List<Company> findAllByFloorAndUser(Floor floor, User user);

}
