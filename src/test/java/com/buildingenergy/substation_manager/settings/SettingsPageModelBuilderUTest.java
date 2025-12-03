package com.buildingenergy.substation_manager.settings;

import com.buildingenergy.substation_manager.formula.dto.CompanyFormulaResponse;
import com.buildingenergy.substation_manager.formula.dto.MeterFormulaResponse;
import com.buildingenergy.substation_manager.formula.service.FormulaService;
import com.buildingenergy.substation_manager.login.service.LoginHistoryService;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.web.dto.EditProfileRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SettingsPageModelBuilderUTest {

    @Mock
    private FormulaService formulaService;
    @Mock
    private LoginHistoryService loginHistoryService;

    @InjectMocks
    private SettingsPageModelBuilder builder;

    @Test
    void build_shouldPopulateAllAttributes() {
        User user = new User();
        user.setId(UUID.randomUUID());

        EditProfileRequest req = new EditProfileRequest();

        when(formulaService.getCompanyFormula(any())).thenReturn(new CompanyFormulaResponse());
        when(formulaService.getMeterFormula(any())).thenReturn(new MeterFormulaResponse());
        when(loginHistoryService.getLoginHistoryForUser(user)).thenReturn(List.of());

        ModelAndView mv = new ModelAndView();

        builder.build(mv, user, req, "profile");

        assertEquals(user, mv.getModel().get("user"));
        assertEquals(req, mv.getModel().get("editProfileRequest"));
        assertEquals("profile", mv.getModel().get("activeTab"));
        assertEquals("settings", mv.getModel().get("currentPage"));
        assertNotNull(mv.getModel().get("formula"));
        assertNotNull(mv.getModel().get("meterFormula"));
        assertNotNull(mv.getModel().get("loginHistory"));
    }

}
