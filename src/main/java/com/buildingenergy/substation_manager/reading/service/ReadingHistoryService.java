package com.buildingenergy.substation_manager.reading.service;

import com.buildingenergy.substation_manager.company.model.Company;
import com.buildingenergy.substation_manager.exception.ReadingNotFound;
import com.buildingenergy.substation_manager.reading.model.Reading;
import com.buildingenergy.substation_manager.reading.model.ReadingHistory;
import com.buildingenergy.substation_manager.reading.repository.ReadingHistoryRepository;
import com.buildingenergy.substation_manager.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Month;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
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

    @Transactional
    public void deleteCompanyByIdAndMonth(UUID readingId, int month) {
        ReadingHistory reading = findById(readingId);

        readingHistoryRepository.deleteByIdAndMonthValue(readingId, month);

        log.info("Deleted reading history for company: [%s] for month [%s]".formatted(reading.getCompanyNameSnapshot(), Month.of(month)));
    }

    public List<ReadingHistory> findAllByUserId(UUID id) {
        return readingHistoryRepository.findAllByUserIdSnapshot(id);
    }

    private ReadingHistory findById(UUID id) {
        return readingHistoryRepository.findById(id).orElseThrow(() -> new ReadingNotFound("Reading with id: [%s] not found.".formatted(id)));
    }
}
