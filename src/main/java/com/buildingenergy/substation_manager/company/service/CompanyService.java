package com.buildingenergy.substation_manager.company.service;

import com.buildingenergy.substation_manager.company.model.Company;
import com.buildingenergy.substation_manager.exception.CompanyNotFound;
import com.buildingenergy.substation_manager.floor.model.Floor;
import com.buildingenergy.substation_manager.reading.model.ReadingHistory;
import com.buildingenergy.substation_manager.reading.service.ReadingHistoryService;
import com.buildingenergy.substation_manager.reading.service.ReadingService;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.company.repository.CompanyRepository;
import com.buildingenergy.substation_manager.floor.service.FloorService;
import com.buildingenergy.substation_manager.web.dto.CompanyView;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CompanyService {

    private final FloorService floorService;
    private final CompanyRepository companyRepository;
    private final ReadingService readingService;
    private final ReadingHistoryService readingHistoryService;

    public CompanyService(FloorService floorService, CompanyRepository companyRepository, ReadingService readingService, ReadingHistoryService readingHistoryService) {
        this.floorService = floorService;
        this.companyRepository = companyRepository;
        this.readingService = readingService;
        this.readingHistoryService = readingHistoryService;
    }

    @Transactional
    @CacheEvict(value = "companyViews", key = "#user.id")
    public void addCompanyForFloor(String companyName, int floorNumber, User user) {
        Floor floor = floorService.findByFloorNumberAndUser(floorNumber, user);

        if (floor == null) {
            floor = floorService.createFloor(floorNumber, user);
        }

        Company company = Company.builder()
                .name(companyName)
                .user(user)
                .floor(floor)
                .createdOn(LocalDateTime.now())
                .build();

        companyRepository.save(company);

        readingService.createDefaultReading(company);
    }

    public List<Company> findAllByFloorAndUser(int floorNumber, User user) {
        Floor floor = floorService.findByFloorNumberAndUser(floorNumber, user);

        return companyRepository.findAllByFloorAndUser(floor, user);
    }

    public List<Company> findTop5ByUser(User user) {
        return companyRepository.findTop5ByUserOrderByCreatedOnDesc(user);
    }

    public List<Company> findAllByUser(User user) {
        return companyRepository.findAllByUser(user);
    }

    public int countByUser(User user) {
        return companyRepository.countByUser(user);
    }

    @Cacheable(value = "companyViews", key = "#user.id")
    public List<CompanyView> getAllWithTotalConsumption(User user) {
        List<Company> companies = findAllByUser(user);
        List<ReadingHistory> readings = readingHistoryService.findAllByUserId(user.getId());

        List<CompanyView> companyViewList = new ArrayList<>();

        for (Company company : companies) {
            BigDecimal total = BigDecimal.ZERO;

            for (ReadingHistory r : readings) {
                if (company.getId().equals(r.getCompanyIdSnapshot())) {
                    total = total.add(r.getTotalConsumption());
                }
            }

            CompanyView companyView = CompanyView.builder()
                    .id(company.getId())
                    .name(company.getName())
                    .floorNumber(company.getFloor().getFloorNumber())
                    .totalConsumption(total.doubleValue())
                    .build();

            companyViewList.add(companyView);
        }

        return companyViewList;
    }

    @Transactional
    @CacheEvict(value = "companyViews", key = "#userId")
    public Company deleteCompany(UUID id, UUID userId) {
        Company company = companyRepository.findByIdAndUser_Id(id, userId).orElseThrow(() -> new CompanyNotFound("Company with id: [%s] for user with id: [%s] was not found.".formatted(id, userId)));

        int floorNumber = company.getFloor().getFloorNumber();
        User user = company.getUser();

        companyRepository.delete(company);

        List<Company> companies = findAllByFloorAndUser(floorNumber, user);

        if (companies.isEmpty() && !floorService.hasMeters(company.getFloor(), user)) {
            floorService.deleteFloorForUser(floorNumber, user);
        }

        return company;
    }
}
