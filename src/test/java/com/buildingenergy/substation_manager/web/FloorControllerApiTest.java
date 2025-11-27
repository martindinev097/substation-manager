package com.buildingenergy.substation_manager.web;

import com.buildingenergy.substation_manager.company.service.CompanyService;
import com.buildingenergy.substation_manager.floor.model.Floor;
import com.buildingenergy.substation_manager.floor.service.FloorService;
import com.buildingenergy.substation_manager.formula.dto.CompanyFormulaResponse;
import com.buildingenergy.substation_manager.formula.service.FormulaService;
import com.buildingenergy.substation_manager.login.handler.LoginFailureHandler;
import com.buildingenergy.substation_manager.login.handler.LoginSuccessHandler;
import com.buildingenergy.substation_manager.reading.service.ReadingService;
import com.buildingenergy.substation_manager.security.UserData;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.user.model.UserRole;
import com.buildingenergy.substation_manager.user.service.UserService;
import com.buildingenergy.substation_manager.web.controller.FloorController;
import com.buildingenergy.substation_manager.web.dto.ReadingListWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FloorController.class)
public class FloorControllerApiTest {

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private CompanyService companyService;
    @MockitoBean
    private ReadingService readingService;
    @MockitoBean
    private FormulaService formulaService;
    @MockitoBean
    private FloorService floorService;
    @MockitoBean
    private LoginSuccessHandler loginSuccessHandler;
    @MockitoBean
    private LoginFailureHandler loginFailureHandler;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getFloorPage_shouldReturnStatus200OkAndShowFloorPageWithAppropriateFloorNumberAndAppropriateAttributes() throws Exception {
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setActive(true);

        CompanyFormulaResponse formula = new CompanyFormulaResponse(
                BigDecimal.valueOf(0.5),
                BigDecimal.ONE,
                BigDecimal.valueOf(100)
        );

        when(userService.getById(userId)).thenReturn(user);
        when(floorService.findByFloorNumberAndUser(2, user)).thenReturn(new Floor());
        when(companyService.findAllByFloorAndUser(2, user)).thenReturn(List.of());
        when(readingService.getWrapperForCompanies(anyList())).thenReturn(new ReadingListWrapper(Collections.emptyList()));
        when(formulaService.getCompanyFormula(userId)).thenReturn(formula);
        when(readingService.areSwapped(any(), any())).thenReturn(false);

        MockHttpServletRequestBuilder httpRequest = get("/floor/" + 2)
                .with(user(authentication(userId)));

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("floor"))
                .andExpect(model().attributeExists(
                        "floorNumber",
                        "currentPage",
                        "companies",
                        "formula",
                        "readingWrapper",
                        "areSwapped"
                ));
    }

    @Test
    void postAddCompany_shouldReturn3xxRedirectAndRedirectToFloorPageAndInvokeAddCompanyForFloor() throws Exception {
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setActive(true);

        when(userService.getById(userId)).thenReturn(user);

        MockHttpServletRequestBuilder httpRequest = post("/floor/" + 2 + "/add-company")
                .param("companyName", "Test")
                .with(user(authentication(userId)))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/floor/" + 2));

        verify(companyService).addCompanyForFloor("Test", 2, user);
    }

    private UserData authentication(UUID userId) {
        return new UserData(userId, "mdinev", "123123", UserRole.USER, true);
    }

}
