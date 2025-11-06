package com.buildingenergy.substation_manager.reading.service;

import com.buildingenergy.substation_manager.company.model.Company;
import com.buildingenergy.substation_manager.company.repository.CompanyRepository;
import com.buildingenergy.substation_manager.exception.CompanyNotFound;
import com.buildingenergy.substation_manager.floor.model.Floor;
import com.buildingenergy.substation_manager.reading.model.Reading;
import com.buildingenergy.substation_manager.reading.repository.ReadingRepository;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.web.dto.ReadingRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReadingService {

    private final ReadingRepository readingRepository;
    private final CompanyRepository companyRepository;

    public ReadingService(ReadingRepository readingRepository, CompanyRepository companyRepository) {
        this.readingRepository = readingRepository;
        this.companyRepository = companyRepository;
    }

    public void createDefaultReading(Company company) {
        Reading defaultReading = Reading.builder()
                .company(company)
                .oldReadingM1(BigDecimal.ZERO)
                .newReadingM1(BigDecimal.ZERO)
                .differenceM1(BigDecimal.ZERO)
                .oldReadingM2(BigDecimal.ZERO)
                .newReadingM2(BigDecimal.ZERO)
                .differenceM2(BigDecimal.ZERO)
                .totalConsumption(BigDecimal.ZERO)
                .totalCost(BigDecimal.ZERO)
                .createdOn(LocalDateTime.now())
                .build();

        readingRepository.save(defaultReading);
    }

    public void updateReadingForCompany(ReadingRequest readingRequest) {
        Company company = companyRepository.findById(readingRequest.getCompanyId()).orElseThrow(() -> new CompanyNotFound("Company with this id [%s] not found.".formatted(readingRequest.getCompanyId())));

        Reading existingReading = readingRepository.findByCompany(company)
                .orElseGet(() -> Reading.builder()
                        .company(company)
                        .createdOn(LocalDateTime.now())
                        .build());

        existingReading.setOldReadingM1(readingRequest.getOldReadingM1());
        existingReading.setNewReadingM1(readingRequest.getNewReadingM1());
        existingReading.setDifferenceM1(readingRequest.getDifferenceM1());
        existingReading.setOldReadingM2(readingRequest.getOldReadingM2());
        existingReading.setNewReadingM2(readingRequest.getNewReadingM2());
        existingReading.setDifferenceM2(readingRequest.getDifferenceM2());
        existingReading.setTotalConsumption(readingRequest.getTotalConsumption());
        existingReading.setTotalCost(readingRequest.getTotalCost());
        existingReading.setCreatedOn(LocalDateTime.now());

        readingRepository.save(existingReading);
    }

    public Reading findByCompany(Company c) {
        return readingRepository.findByCompany(c).orElse(null);
    }

    public void swapAllReadingsForFloor(Floor floor, User user) {
        List<Company> companies = companyRepository.findAllByFloorAndUser(floor, user);

        for (Company company : companies) {
            readingRepository.findByCompany(company).ifPresent(reading -> {
                reading.setOldReadingM1(reading.getNewReadingM1());
                reading.setOldReadingM2(reading.getNewReadingM2());

                reading.setNewReadingM1(BigDecimal.ZERO);
                reading.setNewReadingM2(BigDecimal.ZERO);

                reading.setDifferenceM1(BigDecimal.ZERO);
                reading.setDifferenceM2(BigDecimal.ZERO);
                reading.setTotalConsumption(BigDecimal.ZERO);
                reading.setTotalCost(BigDecimal.ZERO);

                readingRepository.save(reading);
            });
        }
    }

    public List<ReadingRequest> getReadingRequests(List<Company> companies) {

        return companies.stream()
                .map(c -> {
                    Reading reading = findByCompany(c);

                    ReadingRequest dto = new ReadingRequest();
                    dto.setCompanyId(c.getId());
                    dto.setOldReadingM1(reading != null ? reading.getOldReadingM1() : BigDecimal.ZERO);
                    dto.setNewReadingM1(reading != null ? reading.getNewReadingM1() : BigDecimal.ZERO);
                    dto.setDifferenceM1(reading != null ? reading.getDifferenceM1() : BigDecimal.ZERO);
                    dto.setOldReadingM2(reading != null ? reading.getOldReadingM2() : BigDecimal.ZERO);
                    dto.setNewReadingM2(reading != null ? reading.getNewReadingM2() : BigDecimal.ZERO);
                    dto.setDifferenceM2(reading != null ? reading.getDifferenceM2() : BigDecimal.ZERO);
                    dto.setTotalConsumption(reading != null ? reading.getTotalConsumption() : BigDecimal.ZERO);
                    dto.setTotalCost(reading != null ? reading.getTotalCost() : BigDecimal.ZERO);

                    return dto;
                })
                .toList();
    }
}
