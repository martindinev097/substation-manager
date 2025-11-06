package com.buildingenergy.substation_manager.company.service;

import com.buildingenergy.substation_manager.company.model.Company;
import com.buildingenergy.substation_manager.floor.model.Floor;
import com.buildingenergy.substation_manager.reading.service.ReadingService;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.company.repository.CompanyRepository;
import com.buildingenergy.substation_manager.floor.service.FloorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CompanyService {

    private final FloorService floorService;
    private final CompanyRepository companyRepository;
    private final ReadingService readingService;


    public CompanyService(FloorService floorService, CompanyRepository companyRepository, ReadingService readingService) {
        this.floorService = floorService;
        this.companyRepository = companyRepository;
        this.readingService = readingService;
    }

    @Transactional
    public void addCompanyForFloor(String companyName, int floorNumber, User user) {
        Floor floor = floorService.findByFloorNumberAndUser(floorNumber, user);

        if (floor == null) {
            floor = floorService.createFloor(floorNumber, user);
        }

        Company company = Company.builder()
                .name(companyName)
                .user(user)
                .floor(floor)
                .build();

        companyRepository.save(company);

        readingService.createDefaultReading(company);
    }

    public List<Company> findAllByFloorAndUser(int floorNumber, User user) {
        Floor floor = floorService.findByFloorNumberAndUser(floorNumber, user);

        return companyRepository.findAllByFloorAndUser(floor, user);
    }
}
