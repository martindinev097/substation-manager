package com.buildingenergy.substation_manager.web;

import com.buildingenergy.substation_manager.company.service.CompanyService;
import com.buildingenergy.substation_manager.exception.CompanyNotFound;
import com.buildingenergy.substation_manager.login.handler.LoginFailureHandler;
import com.buildingenergy.substation_manager.login.handler.LoginSuccessHandler;
import com.buildingenergy.substation_manager.security.UserData;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.user.model.UserRole;
import com.buildingenergy.substation_manager.user.service.UserService;
import com.buildingenergy.substation_manager.web.controller.OwnerController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OwnerController.class)
public class OwnerControllerApiTest {

    @MockitoBean
    private CompanyService companyService;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private LoginSuccessHandler loginSuccessHandler;
    @MockitoBean
    private LoginFailureHandler loginFailureHandler;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getOwnersPage_shouldReturn200OkWithCompaniesAndCurrentPageAttributes() throws Exception {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("mdinev")
                .password("123123")
                .email("mdinev@gmail.com")
                .isActive(true)
                .role(UserRole.USER)
                .createdOn(LocalDateTime.now())
                .build();

        when(userService.getById(any())).thenReturn(user);
        when(companyService.getAllWithTotalConsumption(user)).thenReturn(List.of());

        UserData authentication = new UserData(UUID.randomUUID(), "mdinev", "123123", UserRole.USER, true);

        MockHttpServletRequestBuilder httpRequest = get("/owners").with(user(authentication));

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("owners"))
                .andExpect(model().attributeExists("currentPage", "companies"));
    }

    @Test
    void deleteCompany_shouldDeleteCompanyAndRedirectToOwnersPage() throws Exception {
        UUID companyId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(userService.getById(userId)).thenReturn(
                User.builder()
                .id(userId)
                .username("mdinev")
                .password("123123")
                .isActive(true)
                .role(UserRole.USER)
                .build()
        );

        UserData authentication = new UserData(userId, "mdinev", "123123", UserRole.USER, true);

        MockHttpServletRequestBuilder httpRequest = delete("/owners/delete/" + companyId)
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/owners"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(companyService).deleteCompany(companyId, userId);
    }

    @Test
    void deleteCompany_whenThereIsNoCompanyReturned_thenCompanyNotFoundExceptionIsInvoked() throws Exception {
        UUID companyId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        UserData authentication = new UserData(userId, "mdinev", "123123", UserRole.USER, true);

        when(userService.getById(userId)).thenReturn(
                User.builder()
                        .id(userId)
                        .username("mdinev")
                        .password("123123")
                        .isActive(true)
                        .role(UserRole.USER)
                        .build()
        );

        doThrow(new CompanyNotFound("Company not found.")).when(companyService).deleteCompany(companyId, userId);

        MockHttpServletRequestBuilder httpRequest = delete("/owners/delete/" + companyId)
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/owners"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

}
