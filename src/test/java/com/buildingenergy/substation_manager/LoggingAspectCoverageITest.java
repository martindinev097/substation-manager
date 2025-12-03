package com.buildingenergy.substation_manager;

import com.buildingenergy.substation_manager.company.model.Company;
import com.buildingenergy.substation_manager.company.service.CompanyService;
import com.buildingenergy.substation_manager.floor.model.Floor;
import com.buildingenergy.substation_manager.meter.model.Meter;
import com.buildingenergy.substation_manager.meter.service.MeterService;
import com.buildingenergy.substation_manager.reading.model.ReadingHistory;
import com.buildingenergy.substation_manager.reading.service.ReadingHistoryService;
import com.buildingenergy.substation_manager.reading.service.ReadingService;
import com.buildingenergy.substation_manager.report.service.ExcelExportService;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.user.service.UserService;
import com.buildingenergy.substation_manager.web.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class LoggingAspectCoverageITest {

    @Autowired
    private UserService userService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private ReadingService readingService;
    @Autowired
    private ReadingHistoryService readingHistoryService;
    @Autowired
    private MeterService meterService;
    @Autowired
    private ExcelExportService excelExportService;

    @Test
    void triggerMostLoggingAspectAdvices() throws Exception {
        RegisterRequest reg = RegisterRequest.builder()
                .username("aspectUser")
                .email("aspect@test.bg")
                .password("123123")
                .confirmedPassword("123123")
                .build();

        User user = userService.register(reg);

        EditProfileRequest editProfileRequest = new EditProfileRequest();
        editProfileRequest.setEmail("new@example.com");
        editProfileRequest.setFirstName("");
        editProfileRequest.setLastName("");

        userService.updateProfile(user, editProfileRequest);
        userService.changeStatus(user, user);
        userService.updateRole(user.getId(), user);

        companyService.addCompanyForFloor("AspectCompany", 1, user);

        Company company = companyService.findAllByUser(user).get(0);

        companyService.deleteCompany(company.getId(), user.getId());
        companyService.addCompanyForFloor("AspectCompany", 1, user);

        company = companyService.findAllByUser(user).get(0);

        Floor floor = company.getFloor();

        MeterRequest mr = new MeterRequest("MeterName", "Outside", "Room1", null);
        meterService.createMeter(mr, floor, floor.getFloorNumber(), user);

        List<Meter> meters = meterService.findAllByFloorAndUser(floor, user);

        meterService.swapMeterReadings(meters);

        MeterReadingRequest mrr = MeterReadingRequest.builder()
                .meterName("MeterName")
                .energyPercentage(BigDecimal.ONE)
                .oldReadings(BigDecimal.ONE)
                .newReadings(BigDecimal.ONE)
                .differenceReadings(BigDecimal.ZERO)
                .totalCost(BigDecimal.ZERO)
                .build();

        meterService.updateMeterReadings(List.of(mrr), user, floor);

        ReadingRequest rr = new ReadingRequest();
        rr.setCompanyId(company.getId());
        rr.setOldReadingM1(BigDecimal.ZERO);
        rr.setNewReadingM1(BigDecimal.ONE);
        rr.setDifferenceM1(BigDecimal.ONE);
        rr.setOldReadingM2(BigDecimal.ZERO);
        rr.setNewReadingM2(BigDecimal.ONE);
        rr.setDifferenceM2(BigDecimal.ONE);
        rr.setTotalConsumption(BigDecimal.ONE);
        rr.setTotalCost(BigDecimal.ONE);
        rr.setOffice("");

        ReadingListWrapper wrapper = new ReadingListWrapper(List.of(rr));

        readingService.updateAllReadings(wrapper, user.getId());
        readingService.swapAllReadingsForFloor(floor, user);

        readingHistoryService.backupCurrentReadings(List.of(company));

        List<ReadingHistory> history = readingHistoryService.findAllByUserId(user.getId());

        if (!history.isEmpty()) {
            ReadingHistory h = history.get(0);
            readingHistoryService.deleteCompanyByIdAndMonth(h.getId(), h.getSavedAt().getMonthValue());
        }

        if (!history.isEmpty()) {
            MockHttpServletResponse response = new MockHttpServletResponse();
            excelExportService.exportReadingHistory(history, response, history.get(0).getSavedAt().getMonthValue());
            assertNotNull(response.getContentAsByteArray());
        }
    }
}
