package com.buildingenergy.substation_manager.settings;

import com.buildingenergy.substation_manager.formula.service.FormulaService;
import com.buildingenergy.substation_manager.login.service.LoginHistoryService;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.web.dto.EditProfileRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

@Service
public class SettingsPageModelBuilder {

    private final FormulaService formulaService;
    private final LoginHistoryService loginHistoryService;

    public SettingsPageModelBuilder(FormulaService formulaService, LoginHistoryService loginHistoryService) {
        this.formulaService = formulaService;
        this.loginHistoryService = loginHistoryService;
    }

    public void build(ModelAndView modelAndView, User user, EditProfileRequest editProfileRequest, String activeTab) {
        modelAndView.addObject("user", user);
        modelAndView.addObject("editProfileRequest", editProfileRequest);
        modelAndView.addObject("formula", formulaService.getCompanyFormula(user.getId()));
        modelAndView.addObject("meterFormula", formulaService.getMeterFormula(user.getId()));
        modelAndView.addObject("loginHistory", loginHistoryService.getLoginHistoryForUser(user));
        modelAndView.addObject("activeTab", activeTab);
        modelAndView.addObject("currentPage", "settings");
    }

}
