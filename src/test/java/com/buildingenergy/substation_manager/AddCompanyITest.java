package com.buildingenergy.substation_manager;

import com.buildingenergy.substation_manager.company.model.Company;
import com.buildingenergy.substation_manager.company.service.CompanyService;
import com.buildingenergy.substation_manager.floor.service.FloorService;
import com.buildingenergy.substation_manager.reading.service.ReadingService;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.user.service.UserService;
import com.buildingenergy.substation_manager.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class AddCompanyITest {

    @Autowired
    private CompanyService companyService;
    @Autowired
    private FloorService floorService;
    @Autowired
    private ReadingService readingService;
    @Autowired
    private UserService userService;

    @Test
    void givenCompanyNameFloorNumberAndUser_whenNoSuchFloorYet_thenCreateFloorAndCreateCompanyAndCreateDefaultReadingForCompany() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("mdinev")
                .email("mdinev@gmail.com")
                .password("123123")
                .confirmedPassword("123123")
                .build();

        User user = userService.register(registerRequest);

        companyService.addCompanyForFloor("TestCompany", 1, user);

        assertEquals("TestCompany", companyService.findAllByUser(user).get(0).getName());
        assertEquals(1, floorService.countByUser(user));

        Company company = companyService.findAllByUser(user).get(0);

        assertEquals(0, readingService.findByCompany(company).getOldReadingM1().compareTo(BigDecimal.ZERO));
    }

}
