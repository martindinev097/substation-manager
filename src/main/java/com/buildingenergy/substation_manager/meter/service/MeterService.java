package com.buildingenergy.substation_manager.meter.service;

import com.buildingenergy.substation_manager.floor.service.FloorService;
import com.buildingenergy.substation_manager.meter.model.Meter;
import com.buildingenergy.substation_manager.meter.repository.MeterRepository;
import com.buildingenergy.substation_manager.floor.model.Floor;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.web.dto.MeterReadingRequest;
import com.buildingenergy.substation_manager.web.dto.MeterRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MeterService {

    private final MeterRepository meterRepository;
    private final FloorService floorService;

    public MeterService(MeterRepository meterRepository, FloorService floorService) {
        this.meterRepository = meterRepository;
        this.floorService = floorService;
    }

    public List<Meter> findAllByFloorAndUser(Floor floor, User user) {
        return meterRepository.findAllByFloorAndUser(floor, user);
    }

    public void createMeter(@Valid MeterRequest meterRequest, Floor floor, int floorNumber, User user) {
        if (floor == null) {
            floor = floorService.createFloor(floorNumber, user);
        }

        Meter meter = Meter.builder()
                .meterName(meterRequest.getMeterName())
                .outsideBody(meterRequest.getOutsideBody())
                .room(meterRequest.getRoom())
                .description(meterRequest.getDescription() == null ? "" : meterRequest.getDescription())
                .energyPercentage(BigDecimal.ZERO)
                .oldReadings(BigDecimal.ZERO)
                .newReadings(BigDecimal.ZERO)
                .user(user)
                .floor(floor)
                .build();

        meterRepository.save(meter);
    }

    public void swapMeterReadings(List<Meter> meters) {
        for (Meter meter : meters) {
            meter.setOldReadings(meter.getNewReadings());
            meter.setNewReadings(BigDecimal.ZERO);
            meter.setDifferenceReadings(BigDecimal.ZERO);
            meter.setTotalCost(BigDecimal.ZERO);

            meterRepository.save(meter);
        }
    }

    public void updateMeterReadings(List<MeterReadingRequest> readings, User user, Floor floor) {
        List<Meter> meters = findAllByFloorAndUser(floor, user);

        for (int i = 0; i <= meters.size() - 1; i++) {
            Meter meter = meters.get(i);
            MeterReadingRequest dto = readings.get(i);

            meter.setEnergyPercentage(dto.getEnergyPercentage());
            meter.setOldReadings(dto.getOldReadings());
            meter.setNewReadings(dto.getNewReadings());
            meter.setDifferenceReadings(dto.getDifferenceReadings());
            meter.setTotalCost(dto.getTotalCost());
        }

        meterRepository.saveAll(meters);
    }

    public List<MeterReadingRequest> getMeterReadings(List<Meter> meters) {
        return meters.stream()
                .map(m -> MeterReadingRequest.builder()
                        .meterName(m.getMeterName())
                        .outsideBody(m.getOutsideBody())
                        .room(m.getRoom())
                        .description(m.getDescription())
                        .energyPercentage(m.getEnergyPercentage())
                        .oldReadings(m.getOldReadings())
                        .newReadings(m.getNewReadings())
                        .differenceReadings(m.getDifferenceReadings())
                        .totalCost(m.getTotalCost())
                        .build())
                .toList();
    }
}
