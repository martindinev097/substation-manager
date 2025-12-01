package com.buildingenergy.substation_manager.floor.repository;

import com.buildingenergy.substation_manager.floor.model.Floor;
import com.buildingenergy.substation_manager.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FloorRepository extends JpaRepository<Floor, UUID> {

    void deleteByFloorNumberAndUser(int floorNumber, User user);

    int countByUser(User user);

    Optional<Floor> findByFloorNumberAndUser_Id(int floorNumber, UUID userId);

}
