package com.buildingenergy.substation_manager.reading;

import com.buildingenergy.substation_manager.company.model.Company;
import com.buildingenergy.substation_manager.reading.model.Reading;
import com.buildingenergy.substation_manager.reading.model.ReadingHistory;
import com.buildingenergy.substation_manager.reading.repository.ReadingHistoryRepository;
import com.buildingenergy.substation_manager.reading.service.ReadingHistoryService;
import com.buildingenergy.substation_manager.reading.service.ReadingService;
import com.buildingenergy.substation_manager.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReadingHistoryServiceUTest {

    @Mock
    private ReadingService readingService;
    @Mock
    private ReadingHistoryRepository historyRepository;

    @InjectMocks
    private ReadingHistoryService historyService;

    @Test
    void givenExistingReadingForCompany_whenBackupCurrentReadings_thenSaveHistoryEntry() {
        User user = new User();
        user.setId(UUID.randomUUID());

        Company company = new Company();
        company.setId(UUID.randomUUID());
        company.setUser(user);
        company.setName("TestCo");

        Reading reading = Reading.builder()
                .company(company)
                .office("A1")
                .oldReadingM1(BigDecimal.ONE)
                .newReadingM1(BigDecimal.TEN)
                .differenceM1(BigDecimal.valueOf(9))
                .oldReadingM2(BigDecimal.ONE)
                .newReadingM2(BigDecimal.TEN)
                .differenceM2(BigDecimal.valueOf(9))
                .totalConsumption(BigDecimal.valueOf(18))
                .totalCost(BigDecimal.valueOf(5))
                .createdOn(LocalDateTime.now())
                .build();

        when(readingService.findByCompany(company)).thenReturn(reading);

        historyService.backupCurrentReadings(List.of(company));

        verify(historyRepository).saveAll(anyList());
    }

    @Test
    void givenCompanyWithNoReading_whenBackupCurrentReadings_thenSkipSavingHistory() {
        Company company = new Company();
        when(readingService.findByCompany(company)).thenReturn(null);

        historyService.backupCurrentReadings(List.of(company));

        verify(historyRepository).saveAll(List.of());
    }

    @Test
    void givenHistoryReadingForUser_whenGetAllByMonth_thenReturnOnlyMatchingMonthAndNonZeroReadings() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        ReadingHistory good1 = new ReadingHistory();
        good1.setNewReadingM1(BigDecimal.ONE);
        good1.setNewReadingM2(BigDecimal.ONE);
        good1.setSavedAt(LocalDateTime.of(2024, 5, 10, 10, 0));

        ReadingHistory zeroReading = new ReadingHistory();
        zeroReading.setNewReadingM1(BigDecimal.ZERO);
        zeroReading.setNewReadingM2(BigDecimal.ONE);
        zeroReading.setSavedAt(LocalDateTime.of(2024, 5, 10, 10, 0));

        ReadingHistory wrongMonth = new ReadingHistory();
        wrongMonth.setNewReadingM1(BigDecimal.ONE);
        wrongMonth.setNewReadingM2(BigDecimal.ONE);
        wrongMonth.setSavedAt(LocalDateTime.of(2024, 6, 10, 10, 0));

        when(historyRepository.findAllByUserIdSnapshotOrderBySavedAtDesc(userId)).thenReturn(List.of(good1, zeroReading, wrongMonth));

        List<ReadingHistory> result = historyService.getAllByMonthAndUser(5, user);

        assertEquals(1, result.size());
        assertEquals(good1, result.get(0));
    }
}