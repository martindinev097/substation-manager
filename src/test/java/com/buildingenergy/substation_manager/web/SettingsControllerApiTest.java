package com.buildingenergy.substation_manager.web;

import com.buildingenergy.substation_manager.exception.EmailAlreadyExists;
import com.buildingenergy.substation_manager.formula.dto.CompanyFormulaRequest;
import com.buildingenergy.substation_manager.formula.dto.CompanyFormulaResponse;
import com.buildingenergy.substation_manager.formula.dto.MeterFormulaRequest;
import com.buildingenergy.substation_manager.formula.dto.MeterFormulaResponse;
import com.buildingenergy.substation_manager.formula.service.FormulaService;
import com.buildingenergy.substation_manager.security.UserData;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.user.model.UserRole;
import com.buildingenergy.substation_manager.user.service.UserService;
import com.buildingenergy.substation_manager.web.controller.SettingsController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SettingsController.class)
public class SettingsControllerApiTest {

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private FormulaService formulaService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getSettingsPage_shouldReturn200OkAndAllAttributesForIt() throws Exception {
        UUID userId = UUID.randomUUID();

        UserData authentication = new UserData(userId, "mdinev", "123123", UserRole.USER, true);

        User user = new User();
        user.setId(userId);
        user.setActive(true);
        user.setRole(UserRole.USER);

        when(userService.getById(userId)).thenReturn(user);
        when(formulaService.getCompanyFormula(userId)).thenReturn(new CompanyFormulaResponse());
        when(formulaService.getMeterFormula(userId)).thenReturn(new MeterFormulaResponse());

        MockHttpServletRequestBuilder httpRequest = get("/settings")
                .param("activeTab", "profile")
                .with(user(authentication));

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("settings"))
                .andExpect(model().attributeExists(
                        "currentPage",
                        "user",
                        "formula",
                        "meterFormula",
                        "editProfileRequest",
                        "activeTab"
                ));
    }

    @Test
    void putUpdateProfile_shouldAddUserAttributeAndSuccessMessageAttributeAndReturn3xxRedirectToSettingsPage() throws Exception {
        UUID userId = UUID.randomUUID();

        UserData authentication = new UserData(userId, "mdinev", "123123", UserRole.USER, true);

        User user = new User();
        user.setId(userId);
        user.setActive(true);
        user.setRole(UserRole.USER);

        when(userService.getById(userId)).thenReturn(user);

        MockHttpServletRequestBuilder httpRequest = put("/settings/profile/update")
                .formField("email", "mdinev@gmail.com")
                .formField("firstName", "")
                .formField("lastName", "")
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService).updateProfile(any(), any());
    }

    @Test
    void putUpdateProfileWithInvalidEditProfileRequest_shouldReturn200OkAndShowSettingsPage() throws Exception {
        UUID userId = UUID.randomUUID();

        UserData authentication = new UserData(userId, "mdinev", "123123", UserRole.USER, true);

        User user = new User();
        user.setId(userId);
        user.setActive(true);
        user.setRole(UserRole.USER);

        when(userService.getById(userId)).thenReturn(user);
        when(formulaService.getCompanyFormula(userId)).thenReturn(new CompanyFormulaResponse());
        when(formulaService.getMeterFormula(userId)).thenReturn(new MeterFormulaResponse());

        MockHttpServletRequestBuilder httpRequest = put("/settings/profile/update")
                .formField("email", "mdinev")
                .formField("firstName", "")
                .formField("lastName", "")
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("settings"))
                .andExpect(model().attributeExists(
                        "user",
                        "editProfileRequest",
                        "formula",
                        "meterFormula",
                        "activeTab"
                ));

        verify(userService, never()).updateProfile(any(), any());
    }

    @Test
    void putUpdateProfileWithAlreadyExistingEmail_shouldReturn200OkAndThrowEmailAlreadyExistsExceptionAndAddTheAttributesForThePage() throws Exception {
        UUID userId = UUID.randomUUID();

        UserData authentication = new UserData(userId, "mdinev", "123123", UserRole.USER, true);

        User user = new User();
        user.setId(userId);
        user.setActive(true);
        user.setRole(UserRole.USER);

        when(userService.getById(userId)).thenReturn(user);
        when(formulaService.getCompanyFormula(userId)).thenReturn(new CompanyFormulaResponse());
        when(formulaService.getMeterFormula(userId)).thenReturn(new MeterFormulaResponse());

        doThrow(new EmailAlreadyExists("Email exists.")).when(userService).updateProfile(any(), any());

        MockHttpServletRequestBuilder httpRequest = put("/settings/profile/update")
                .formField("email", "mdinev@gmail.com")
                .formField("firstName", "")
                .formField("lastName", "")
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("settings"))
                .andExpect(model().attributeExists(
                        "user",
                        "editProfileRequest",
                        "formula",
                        "meterFormula",
                        "activeTab",
                        "emailExistsMessage"
                ));
    }

    @Test
    void putUpdateCompanyFormula_shouldReturn3xxRedirectToSettingsPageWithTabParamAndSuccessUpdateMessageFlashAttribute() throws Exception {
        UUID userId = UUID.randomUUID();

        UserData authentication = new UserData(userId, "mdinev", "123123", UserRole.USER, true);

        User user = new User();
        user.setId(userId);
        user.setActive(true);
        user.setRole(UserRole.USER);

        when(userService.getById(userId)).thenReturn(user);
        when(formulaService.updateCompanyFormula(userId, new CompanyFormulaRequest())).thenReturn(true);

        MockHttpServletRequestBuilder httpRequest = put("/settings/company/formula/update")
                .formField("pricePerKwh", "0.2")
                .formField("multiplier", "2")
                .formField("divider", "3")
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings?tab=settings"))
                .andExpect(flash().attributeExists("successUpdateMessage"));

        verify(formulaService).updateCompanyFormula(any(), any());
    }

    @Test
    void putUpdateCompanyFormulaWithInvalidRequest_shouldReturn3xxRedirectToSettingsPageWithTabParamAndErrorCompanyFormulaMessageFlashAttribute() throws Exception {
        UUID userId = UUID.randomUUID();

        UserData authentication = new UserData(userId, "mdinev", "123123", UserRole.USER, true);

        User user = new User();
        user.setId(userId);
        user.setActive(true);
        user.setRole(UserRole.USER);

        when(userService.getById(userId)).thenReturn(user);

        MockHttpServletRequestBuilder httpRequest = put("/settings/company/formula/update")
                .formField("pricePerKwh", "")
                .formField("multiplier", "2")
                .formField("divider", "3")
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings?tab=settings"))
                .andExpect(flash().attributeExists("errorCompanyFormulaMessage"));

        verify(formulaService, never()).updateCompanyFormula(any(), any());
    }

    @Test
    void putUpdateMeterFormula_shouldReturn3xxRedirectToSettingsPageWithTabParamAndMeterSuccessFlashAttribute() throws Exception {
        UUID userId = UUID.randomUUID();

        UserData authentication = new UserData(userId, "mdinev", "123123", UserRole.USER, true);

        User user = new User();
        user.setId(userId);
        user.setActive(true);
        user.setRole(UserRole.USER);

        when(userService.getById(userId)).thenReturn(user);
        when(formulaService.updateMeterFormula(userId, new MeterFormulaRequest())).thenReturn(true);

        MockHttpServletRequestBuilder httpRequest = put("/settings/meter/formula/update")
                .formField("pricePerKwh", "0.2")
                .formField("divider", "3")
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings?tab=settings"))
                .andExpect(flash().attributeExists("meterSuccess"));

        verify(formulaService).updateMeterFormula(any(), any());
    }

    @Test
    void putUpdateMeterFormulaWithInvalidRequest_shouldReturn3xxRedirectToSettingsPageWithTabParamAndErrorMeterFormulaMessageFlashAttribute() throws Exception {
        UUID userId = UUID.randomUUID();

        UserData authentication = new UserData(userId, "mdinev", "123123", UserRole.USER, true);

        User user = new User();
        user.setId(userId);
        user.setActive(true);
        user.setRole(UserRole.USER);

        when(userService.getById(userId)).thenReturn(user);

        MockHttpServletRequestBuilder httpRequest = put("/settings/meter/formula/update")
                .formField("pricePerKwh", "")
                .formField("divider", "3")
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings?tab=settings"))
                .andExpect(flash().attributeExists("errorMeterFormulaMessage"));

        verify(formulaService, never()).updateMeterFormula(any(), any());
    }

}
