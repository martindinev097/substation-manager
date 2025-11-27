package com.buildingenergy.substation_manager.meter;

import com.buildingenergy.substation_manager.meter.model.Meter;
import com.buildingenergy.substation_manager.meter.model.MeterHistory;
import com.buildingenergy.substation_manager.meter.repository.MeterHistoryRepository;
import com.buildingenergy.substation_manager.meter.service.MeterHistoryService;
import com.buildingenergy.substation_manager.user.model.User;
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
public class MeterHistoryServiceUTest {

    @Mock
    private MeterHistoryRepository meterHistoryRepository;

    @InjectMocks
    private MeterHistoryService meterHistoryService;

    @Test
    void givenMeters_whenBackupCurrentReadings_thenSaveHistoryEntries() {
        User user = new User();

        Meter meter = new Meter();
        meter.setMeterName("Main Meter");
        meter.setRoom("1");
        meter.setDescription("Desc");
        meter.setOutsideBody("Body");
        meter.setEnergyPercentage(BigDecimal.valueOf(50));
        meter.setOldReadings(BigDecimal.valueOf(100));
        meter.setNewReadings(BigDecimal.valueOf(150));
        meter.setDifferenceReadings(BigDecimal.valueOf(50));
        meter.setTotalCost(BigDecimal.valueOf(25));
        meter.setCreatedOn(LocalDateTime.now());

        List<Meter> meters = List.of(meter);

        meterHistoryService.backupCurrentReadings(meters, user);

        verify(meterHistoryRepository, times(1)).saveAll(anyList());
    }

    @Test
    void givenHistoryReadingForUser_whenGetAllByMonth_thenReturnOnlyMatchingMonthAndNonZeroReadings() {
        User user = new User();
        int targetMonth = 5;

        MeterHistory h1 = new MeterHistory();
        h1.setNewReadings(BigDecimal.valueOf(100));
        h1.setSavedAt(LocalDateTime.of(2025, 5, 10, 12, 0));

        MeterHistory h2 = new MeterHistory();
        h2.setNewReadings(BigDecimal.valueOf(0));
        h2.setSavedAt(LocalDateTime.of(2025, 5, 12, 12, 0));

        MeterHistory h3 = new MeterHistory();
        h3.setNewReadings(BigDecimal.valueOf(50));
        h3.setSavedAt(LocalDateTime.of(2025, 6, 1, 12, 0));

        when(meterHistoryRepository.findAllByUserIdSnapshotOrderBySavedAtDesc(user.getId())).thenReturn(List.of(h1, h2, h3));

        List<MeterHistory> result = meterHistoryService.getAllByMonthAndUser(targetMonth, user);

        assertEquals(1, result.size());
        assertEquals(h1, result.get(0));
    }
}
