package com.buildingenergy.substation_manager.reading.service;

import com.buildingenergy.substation_manager.company.model.Company;
import com.buildingenergy.substation_manager.reading.model.Reading;
import com.buildingenergy.substation_manager.reading.model.ReadingHistory;
import com.buildingenergy.substation_manager.reading.repository.ReadingHistoryRepository;
import com.buildingenergy.substation_manager.user.model.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class ReadingHistoryService {

    private final ReadingService readingService;
    private final ReadingHistoryRepository readingHistoryRepository;

    public ReadingHistoryService(ReadingService readingService, ReadingHistoryRepository readingHistoryRepository) {
        this.readingService = readingService;
        this.readingHistoryRepository = readingHistoryRepository;
    }

    public void backupCurrentReadings(List<Company> companies) {
        List<Reading> readings = companies.stream().map(readingService::findByCompany).filter(Objects::nonNull).toList();

        List<ReadingHistory> readingHistory = readings.stream()
                .map(r -> ReadingHistory.builder()
                        .companyIdSnapshot(r.getCompany().getId())
                        .userIdSnapshot(r.getCompany().getUser().getId())
                        .companyNameSnapshot(r.getCompany().getName())
                        .office(r.getOffice())
                        .oldReadingM1(r.getOldReadingM1())
                        .newReadingM1(r.getNewReadingM1())
                        .differenceM1(r.getDifferenceM1())
                        .oldReadingM2(r.getOldReadingM2())
                        .newReadingM2(r.getNewReadingM2())
                        .differenceM2(r.getDifferenceM2())
                        .totalConsumption(r.getTotalConsumption())
                        .totalCost(r.getTotalCost())
                        .savedAt(r.getCreatedOn())
                        .build()).toList();

        readingHistoryRepository.saveAll(readingHistory);
    }

    public List<ReadingHistory> getAllByMonthAndUser(int month, User user) {
        return readingHistoryRepository.findAllByUserIdSnapshotOrderBySavedAtDesc(user.getId())
                .stream()
                .filter(r -> r.getNewReadingM1().compareTo(BigDecimal.ZERO) != 0 && r.getNewReadingM2().compareTo(BigDecimal.ZERO) != 0)
                .filter(r -> r.getSavedAt().getMonthValue() == month)
                .toList();
    }
}
