package com.buildingenergy.substation_manager.floor.service;

import com.buildingenergy.substation_manager.exception.FloorNotFound;
import com.buildingenergy.substation_manager.floor.model.Floor;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.floor.repository.FloorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FloorService {

    private final FloorRepository floorRepository;

    public FloorService(FloorRepository floorRepository) {
        this.floorRepository = floorRepository;
    }

    public Floor findByFloorNumberAndUser(int floorNumber, User user) {
        if (floorNumber < 1 || floorNumber > 5) {
            throw new FloorNotFound("Floor number %d was not found.".formatted(floorNumber));
        }

        return floorRepository.findByFloorNumberAndUser(floorNumber, user).orElse(null);
    }

    public Floor createFloor(int floorNumber, User user) {
        Floor floor = Floor.builder()
                .floorNumber(floorNumber)
                .user(user)
                .build();

        return floorRepository.save(floor);
    }

    public List<Floor> findAllByUser(User user) {
        return floorRepository.findAllByUser(user);
    }
}
