package com.buildingenergy.substation_manager.meter.repository;

import com.buildingenergy.substation_manager.meter.model.Meter;
import com.buildingenergy.substation_manager.floor.model.Floor;
import com.buildingenergy.substation_manager.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MeterRepository extends JpaRepository<Meter, UUID> {

    List<Meter> findAllByFloorAndUser(Floor floor, User user);

    int countByUser(User user);

    boolean existsByFloorAndUser(Floor floor, User user);
}
