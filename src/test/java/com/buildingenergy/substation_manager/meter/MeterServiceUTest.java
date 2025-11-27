package com.buildingenergy.substation_manager.meter;

import com.buildingenergy.substation_manager.floor.model.Floor;
import com.buildingenergy.substation_manager.floor.service.FloorService;
import com.buildingenergy.substation_manager.meter.model.Meter;
import com.buildingenergy.substation_manager.meter.repository.MeterRepository;
import com.buildingenergy.substation_manager.meter.service.MeterService;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.web.dto.MeterReadingRequest;
import com.buildingenergy.substation_manager.web.dto.MeterReadingWrapper;
import com.buildingenergy.substation_manager.web.dto.MeterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MeterServiceUTest {

    @Mock
    private MeterRepository meterRepository;
    @Mock
    private FloorService floorService;

    @InjectMocks
    private MeterService meterService;

    @Test
    void givenFloorAndUser_whenFindAllByFloorAndUser_thenReturnMeters() {
        Floor floor = new Floor();

        User user = new User();

        when(meterRepository.findAllByFloorAndUser(floor, user)).thenReturn(List.of(new Meter()));

        List<Meter> result = meterService.findAllByFloorAndUser(floor, user);

        assertEquals(1, result.size());

        verify(meterRepository).findAllByFloorAndUser(floor, user);
    }

    @Test
    void givenNullFloor_whenCreateMeter_thenCreateFloorAndSaveMeter() {
        User user = new User();

        MeterRequest req = new MeterRequest();
        req.setMeterName("Main Meter");
        req.setOutsideBody("Body");
        req.setRoom("101");

        Floor createdFloor = new Floor();

        when(floorService.createFloor(2, user)).thenReturn(createdFloor);

        meterService.createMeter(req, null, 2, user);

        verify(floorService).createFloor(2, user);
        verify(meterRepository).save(any(Meter.class));
    }

    @Test
    void givenExistingFloor_whenCreateMeter_thenSaveMeterWithoutCreatingFloor() {
        User user = new User();

        MeterRequest req = new MeterRequest();
        req.setMeterName("19");

        Floor floor = new Floor();

        meterService.createMeter(req, floor, 1, user);

        verify(floorService, never()).createFloor(anyInt(), any());
        verify(meterRepository).save(any(Meter.class));
    }

    @Test
    void givenMeterWithNewReadings_whenSwapMeterReadings_thenMoveNewToOldReadings() {
        Meter m = new Meter();
        m.setNewReadings(BigDecimal.valueOf(50));
        m.setDifferenceReadings(BigDecimal.valueOf(10));
        m.setTotalCost(BigDecimal.valueOf(5));
        m.setUser(new User());

        meterService.swapMeterReadings(List.of(m));

        assertEquals(BigDecimal.valueOf(50), m.getOldReadings());
        assertEquals(BigDecimal.ZERO, m.getNewReadings());

        verify(meterRepository).save(m);
    }

    @Test
    void givenMeterReadingsDto_whenUpdateMeterReadings_thenUpdateMeterValuesAndSaveAll() {
        User user = new User();
        Floor floor = new Floor();

        Meter m = new Meter();
        MeterReadingRequest dto = new MeterReadingRequest();

        dto.setEnergyPercentage(BigDecimal.TEN);
        dto.setOldReadings(BigDecimal.ONE);
        dto.setNewReadings(BigDecimal.valueOf(5));
        dto.setDifferenceReadings(BigDecimal.valueOf(4));
        dto.setTotalCost(BigDecimal.valueOf(2));

        when(meterRepository.findAllByFloorAndUser(floor, user)).thenReturn(List.of(m));

        meterService.updateMeterReadings(List.of(dto), user, floor);

        assertEquals(BigDecimal.TEN, m.getEnergyPercentage());
        assertEquals(BigDecimal.ONE, m.getOldReadings());
        assertEquals(BigDecimal.valueOf(5), m.getNewReadings());
        assertEquals(BigDecimal.valueOf(4), m.getDifferenceReadings());
        assertEquals(BigDecimal.valueOf(2), m.getTotalCost());

        verify(meterRepository).saveAll(anyList());
    }

    @Test
    void givenMeters_whenGetMeterReadings_thenReturnMappedMeterReadingRequests() {
        Meter m = new Meter();
        m.setMeterName("M1");
        m.setOutsideBody("Body");
        m.setRoom("101");
        m.setDescription("Desc");
        m.setEnergyPercentage(BigDecimal.TEN);
        m.setOldReadings(BigDecimal.ONE);
        m.setNewReadings(BigDecimal.TEN);
        m.setDifferenceReadings(BigDecimal.valueOf(9));
        m.setTotalCost(BigDecimal.valueOf(4));
        m.setCreatedOn(LocalDateTime.now());

        List<MeterReadingRequest> result = meterService.getMeterReadings(List.of(m));

        assertEquals(1, result.size());
        assertEquals("M1", result.get(0).getMeterName());
    }

    @Test
    void givenUser_whenCountByUser_thenReturnMetersCountForTheUser() {
        User user = new User();

        Floor floor = Floor.builder()
                .user(user)
                .build();

        Meter m1 = Meter.builder()
                .meterName("19")
                .outsideBody("2")
                .createdOn(LocalDateTime.now())
                .floor(floor)
                .user(user)
                .build();

        Meter m2 = Meter.builder()
                .meterName("32a")
                .outsideBody("3")
                .createdOn(LocalDateTime.now())
                .floor(floor)
                .user(user)
                .build();

        when(meterRepository.countByUser(user)).thenReturn(List.of(m1, m2).size());

        int result = meterService.countByUser(user);

        assertEquals(2, result);

        verify(meterRepository).countByUser(user);
    }

    @Test
    void givenMeters_whenBuildMeterReadingWrapper_thenReturnWrapperWithMappedReadings() {
        Meter m = new Meter();
        m.setMeterName("Test");

        MeterReadingWrapper wrapper = meterService.buildMeterReadingWrapper(List.of(m));

        assertEquals(1, wrapper.getReadings().size());
    }

    @Test
    void givenAllMetersWithZeroNewReadings_whenAreSwapped_thenReturnTrue() {
        User user = new User();
        Floor floor = new Floor();

        Meter m1 = new Meter();
        m1.setNewReadings(BigDecimal.ZERO);

        Meter m2 = new Meter();
        m2.setNewReadings(BigDecimal.ZERO);

        when(meterRepository.findAllByFloorAndUser(floor, user)).thenReturn(List.of(m1, m2));

        assertTrue(meterService.areSwapped(user, floor));
    }

    @Test
    void givenSomeMetersWithNonZeroNewReadings_whenAreSwapped_thenReturnFalse() {
        User user = new User();
        Floor floor = new Floor();

        Meter m1 = new Meter();
        m1.setNewReadings(BigDecimal.ZERO);

        Meter m2 = new Meter();
        m2.setNewReadings(BigDecimal.ONE);

        when(meterRepository.findAllByFloorAndUser(floor, user)).thenReturn(List.of(m1, m2));

        assertFalse(meterService.areSwapped(user, floor));
    }
}