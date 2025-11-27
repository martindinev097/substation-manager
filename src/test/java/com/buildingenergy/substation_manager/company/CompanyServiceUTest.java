package com.buildingenergy.substation_manager.company;

import com.buildingenergy.substation_manager.company.model.Company;
import com.buildingenergy.substation_manager.company.repository.CompanyRepository;
import com.buildingenergy.substation_manager.company.service.CompanyService;
import com.buildingenergy.substation_manager.floor.model.Floor;
import com.buildingenergy.substation_manager.floor.service.FloorService;
import com.buildingenergy.substation_manager.reading.model.Reading;
import com.buildingenergy.substation_manager.reading.model.ReadingHistory;
import com.buildingenergy.substation_manager.reading.service.ReadingHistoryService;
import com.buildingenergy.substation_manager.reading.service.ReadingService;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.web.dto.CompanyView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompanyServiceUTest {

    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private FloorService floorService;
    @Mock
    private ReadingService readingService;
    @Mock
    private ReadingHistoryService readingHistoryService;

    @InjectMocks
    private CompanyService companyService;

    @Test
    void givenUserWithCompaniesAndFloor_whenGetAllWithTotalConsumption_thenReturnMappedViews() {
        User user = new User();
        user.setId(UUID.randomUUID());

        Floor floor = new Floor();
        floor.setFloorNumber(2);

        Company company = Company.builder()
                .id(UUID.randomUUID())
                .name("TestCompany")
                .floor(floor)
                .user(user)
                .build();

        Reading r1 = Reading.builder()
                .oldReadingM1(BigDecimal.valueOf(10))
                .newReadingM1(BigDecimal.valueOf(15))
                .oldReadingM2(BigDecimal.valueOf(20))
                .newReadingM2(BigDecimal.valueOf(30))
                .build();

        ReadingHistory rh1 = ReadingHistory.builder()
                .oldReadingM1(r1.getOldReadingM1())
                .newReadingM1(r1.getNewReadingM1())
                .oldReadingM2(r1.getOldReadingM2())
                .newReadingM2(r1.getNewReadingM2())
                .totalConsumption(
                        r1.getNewReadingM1().subtract(r1.getOldReadingM1())
                                .add(r1.getNewReadingM2().subtract(r1.getOldReadingM2()))
                )
                .build();

        Reading r2 = Reading.builder()
                .oldReadingM1(BigDecimal.valueOf(50))
                .newReadingM1(BigDecimal.valueOf(60))
                .oldReadingM2(BigDecimal.valueOf(100))
                .newReadingM2(BigDecimal.valueOf(125))
                .build();

        ReadingHistory rh2 = ReadingHistory.builder()
                .oldReadingM1(r2.getOldReadingM1())
                .newReadingM1(r2.getNewReadingM1())
                .oldReadingM2(r2.getOldReadingM2())
                .newReadingM2(r2.getNewReadingM2())
                .totalConsumption(
                        r2.getNewReadingM1().subtract(r2.getOldReadingM1())
                                .add(r2.getNewReadingM2().subtract(r2.getOldReadingM2()))
                )
                .build();

        company.setReadings(List.of(r1, r2));

        rh1.setCompanyNameSnapshot(company.getName());
        rh1.setCompanyIdSnapshot(company.getId());
        rh2.setCompanyNameSnapshot(company.getName());
        rh2.setCompanyIdSnapshot(company.getId());

        List<ReadingHistory> readings = List.of(rh1, rh2);

        when(companyRepository.findAllByUser(user)).thenReturn(List.of(company));
        when(readingHistoryService.findAllByUserId(user.getId())).thenReturn(List.of(rh1, rh2));

        List<CompanyView> result = companyService.getAllWithTotalConsumption(user);

        assertEquals(1, result.size());

        CompanyView view = result.get(0);

        assertEquals("TestCompany", readings.get(0).getCompanyNameSnapshot());
        assertEquals(2, view.getFloorNumber());
        assertEquals(50.0, view.getTotalConsumption());

        verify(companyRepository, times(1)).findAllByUser(user);
    }

    @Test
    void givenUser_whenFindTop5ByUser_thenReturnLatestFiveCompanies() {
        User user = new User();

        when(companyRepository.findTop5ByUserOrderByCreatedOnDesc(user)).thenReturn(List.of());

        List<Company> result = companyService.findTop5ByUser(user);

        assertTrue(result.isEmpty());

        verify(companyRepository).findTop5ByUserOrderByCreatedOnDesc(user);
    }

    @Test
    void givenFloorNumberAndUser_whenFindAllByFloorAndUser_thenReturnCompaniesForFloor() {
        User user = new User();
        Floor floor = new Floor();

        when(floorService.findByFloorNumberAndUser(2, user)).thenReturn(floor);
        when(companyRepository.findAllByFloorAndUser(floor, user)).thenReturn(List.of());

        List<Company> result = companyService.findAllByFloorAndUser(2, user);

        assertTrue(result.isEmpty());

        verify(floorService).findByFloorNumberAndUser(2, user);
        verify(companyRepository).findAllByFloorAndUser(floor, user);
    }

    @Test
    void givenExistingCompanyForUser_whenDeleteCompany_thenRemoveCompany() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Company company = new Company();
        company.setUser(new User());

        Floor floor = new Floor();
        company.setFloor(floor);

        when(companyRepository.findByIdAndUser_Id(id, userId)).thenReturn(Optional.of(company));

        companyService.deleteCompany(id, userId);

        verify(companyRepository).delete(company);
    }

    @Test
    void givenValidFloorNumber_whenAddCompanyForFloor_thenCreateCompanyAndInitializeReading() {
        User user = new User();

        Floor floor = new Floor();

        when(floorService.findByFloorNumberAndUser(3, user)).thenReturn(floor);

        companyService.addCompanyForFloor("NewCo", 3, user);

        verify(companyRepository).save(any(Company.class));
        verify(readingService).createDefaultReading(any(Company.class));
    }

}
