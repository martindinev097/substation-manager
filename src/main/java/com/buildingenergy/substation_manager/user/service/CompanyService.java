package com.buildingenergy.substation_manager.user.service;

import com.buildingenergy.substation_manager.user.model.Company;
import com.buildingenergy.substation_manager.user.model.Floor;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.user.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {

    private final FloorService floorService;
    private final CompanyRepository companyRepository;

    public CompanyService(FloorService floorService, CompanyRepository companyRepository) {
        this.floorService = floorService;
        this.companyRepository = companyRepository;
    }

    public void addCompanyForFloor(String companyName, int floorNumber, User user) {
        Floor floor = floorService.findByFloorNumberAndUser(floorNumber, user);

        Company company = Company.builder()
                .name(companyName)
                .user(user)
                .floor(floor)
                .build();

        companyRepository.save(company);
    }

    public List<Company> findAllByFloorAndUser(int floorNumber, User user) {
        Floor floor = floorService.findByFloorNumberAndUser(floorNumber, user);

        return companyRepository.findAllByFloorAndUser(floor, user);
    }
}
