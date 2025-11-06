package com.buildingenergy.substation_manager.floor.service;

import com.buildingenergy.substation_manager.floor.model.Floor;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.floor.repository.FloorRepository;
import org.springframework.stereotype.Service;

@Service
public class FloorService {

    private final FloorRepository floorRepository;

    public FloorService(FloorRepository floorRepository) {
        this.floorRepository = floorRepository;
    }

    public Floor findByFloorNumberAndUser(int floorNumber, User user) {
        return floorRepository.findByFloorNumberAndUser(floorNumber, user).orElse(null);
    }

    public Floor createFloor(int floorNumber, User user) {
        Floor floor = Floor.builder()
                .floorNumber(floorNumber)
                .user(user)
                .build();

        return floorRepository.save(floor);
    }
}
