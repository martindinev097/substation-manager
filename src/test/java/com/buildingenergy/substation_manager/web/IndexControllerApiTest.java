package com.buildingenergy.substation_manager.web;

import com.buildingenergy.substation_manager.company.model.Company;
import com.buildingenergy.substation_manager.company.service.CompanyService;
import com.buildingenergy.substation_manager.exception.EmailAlreadyExists;
import com.buildingenergy.substation_manager.floor.model.Floor;
import com.buildingenergy.substation_manager.floor.service.FloorService;
import com.buildingenergy.substation_manager.meter.service.MeterService;
import com.buildingenergy.substation_manager.reading.model.Reading;
import com.buildingenergy.substation_manager.security.UserData;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.user.model.UserRole;
import com.buildingenergy.substation_manager.user.service.UserService;
import com.buildingenergy.substation_manager.web.controller.IndexController;
import com.buildingenergy.substation_manager.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IndexController.class)
public class IndexControllerApiTest {

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private CompanyService companyService;
    @MockitoBean
    private FloorService floorService;
    @MockitoBean
    private MeterService meterService;

    @Autowired
    private MockMvc mockMvc;

    @Captor
    private ArgumentCaptor<RegisterRequest> registerRequestArgumentCaptor;

    @Test
    void getLoginEndpoint_shouldReturn200OkAndLoginPageView() throws Exception {
        MockHttpServletRequestBuilder builder = get("/login");

        mockMvc.perform(builder)
                .andExpect(view().name("login"))
                .andExpect(status().is(200));
    }

    @Test
    void getRegisterEndpoint_shouldReturn200OkAndRegisterPageView() throws Exception {
        MockHttpServletRequestBuilder builder = get("/register");

        mockMvc.perform(builder)
                .andExpect(view().name("register"))
                .andExpect(status().is(200));
    }

    @Test
    void postRegisterEndpoint_shouldReturn302RedirectAndRedirectToLoginPageAndInvokeRegisterServiceMethod() throws Exception {
        MockHttpServletRequestBuilder builder = post("/register")
                .formField("username", "mdinev")
                .formField("email", "mdinev@abv.bg")
                .formField("password", "123123")
                .formField("confirmedPassword", "123123")
                .with(csrf());

        mockMvc.perform(builder)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        verify(userService).register(registerRequestArgumentCaptor.capture());

        RegisterRequest registerRequest = registerRequestArgumentCaptor.getValue();

        assertEquals("mdinev", registerRequest.getUsername());
        assertEquals("mdinev@abv.bg", registerRequest.getEmail());
        assertEquals("123123", registerRequest.getPassword());
        assertEquals("123123", registerRequest.getConfirmedPassword());
    }

    @Test
    void postRegisterEndpointWithInvalidFormData_shouldReturn200OkAndShowRegisterViewAndRegisterMethodIsNeverInvoked() throws Exception {
        MockHttpServletRequestBuilder builder = post("/register")
                .formField("username", "mv")
                .formField("email", "mdinev")
                .formField("password", "123123")
                .formField("confirmedPassword", "123123")
                .with(csrf());

        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(view().name("register"));

        verify(userService, never()).register(any());
    }

    @Test
    void postRegisterEndpointWithAlreadyExistingEmail_shouldReturn200OkAndHandleEmailAlreadyExistsExceptionAndShowRegisterPageAgain () throws Exception {
        when(userService.register(any())).thenThrow(new EmailAlreadyExists("Email already exists"));

        MockHttpServletRequestBuilder builder = post("/register")
                .formField("username", "mdinev")
                .formField("email", "mdinev@gmail.com")
                .formField("password", "123123")
                .formField("confirmedPassword", "123123")
                .with(csrf());

        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registerRequest", "emailExistsMessage"));
    }

    @Test
    void getHomePage_shouldReturn200OkAndHomePageWithUserAttributeAndCompanyCountAndFloorCountAndMeterCountAndCurrentPageAttributes() throws Exception {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("mdinev")
                .password("123123")
                .email("mdinev@gmail.com")
                .isActive(true)
                .role(UserRole.USER)
                .createdOn(LocalDateTime.now())
                .build();

        Floor f1 = Floor.builder()
                .floorNumber(1)
                .build();

        Company c1 = Company.builder()
                .name("Test1")
                .floor(f1)
                .createdOn(LocalDateTime.now())
                .build();
        Company c2 = Company.builder()
                .name("Test2")
                .floor(f1)
                .createdOn(LocalDateTime.now())
                .build();

        Company c3 = Company.builder()
                .name("Test3")
                .floor(f1)
                .createdOn(LocalDateTime.now())
                .build();

        Reading r1 = Reading.builder()
                .company(c1)
                .build();

        Reading r2 = Reading.builder()
                .company(c2)
                .build();

        Reading r3 = Reading.builder()
                .company(c3)
                .build();

        c1.setReadings(List.of(r1));
        c2.setReadings(List.of(r2));
        c3.setReadings(List.of(r3));

        when(userService.getById(any())).thenReturn(user);
        when(companyService.findTop5ByUser(user)).thenReturn(List.of(c1, c2, c3));
        when(floorService.countByUser(user)).thenReturn(1);
        when(meterService.countByUser(user)).thenReturn(0);
        when(companyService.countByUser(user)).thenReturn(3);

        UserData authentication = new UserData(user.getId(), user.getUsername(), user.getPassword(), user.getRole(), user.isActive());

        MockHttpServletRequestBuilder httpRequest = get("/home").with(user(authentication));

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists(
                        "user",
                        "currentPage",
                        "companies",
                        "floorCount",
                        "meterCount",
                        "companyCount"
                        ));
    }

}
