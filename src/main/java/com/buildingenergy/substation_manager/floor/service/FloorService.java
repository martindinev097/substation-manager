package com.buildingenergy.substation_manager.floor.service;

import com.buildingenergy.substation_manager.exception.FloorNotFound;
import com.buildingenergy.substation_manager.floor.model.Floor;
import com.buildingenergy.substation_manager.meter.repository.MeterRepository;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.floor.repository.FloorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class FloorService {

    private final FloorRepository floorRepository;
    private final MeterRepository meterRepository;

    public FloorService(FloorRepository floorRepository, MeterRepository meterRepository) {
        this.floorRepository = floorRepository;
        this.meterRepository = meterRepository;
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

    public int countByUser(User user) {
        return floorRepository.countByUser(user);
    }

    public boolean hasMeters(Floor floor, User user) {
        return meterRepository.existsByFloorAndUser(floor, user);
    }

    @Transactional
    public void deleteFloorForUser(int floorNumber, User user) {
        floorRepository.deleteByFloorNumberAndUser(floorNumber, user);
    }
}
