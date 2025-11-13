package com.buildingenergy.substation_manager.web.controller;

import com.buildingenergy.substation_manager.config.FormulaConfiguration;
import com.buildingenergy.substation_manager.exception.EmailAlreadyExists;
import com.buildingenergy.substation_manager.formula.client.CompanyFormulaClient;
import com.buildingenergy.substation_manager.formula.dto.CompanyFormulaRequest;
import com.buildingenergy.substation_manager.formula.dto.CompanyFormulaResponse;
import com.buildingenergy.substation_manager.security.UserData;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.user.service.UserService;
import com.buildingenergy.substation_manager.web.dto.EditProfileRequest;
import com.buildingenergy.substation_manager.web.mapper.DtoMapper;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/settings")
public class SettingsController {

    private final UserService userService;
    private final CompanyFormulaClient formulaClient;
    private final FormulaConfiguration formulaConfig;

    public SettingsController(UserService userService, CompanyFormulaClient formulaClient, FormulaConfiguration formulaConfig) {
        this.userService = userService;
        this.formulaClient = formulaClient;
        this.formulaConfig = formulaConfig;
    }

    @GetMapping
    public ModelAndView getSettingsPage(@AuthenticationPrincipal UserData userData) {
        ModelAndView modelAndView = new ModelAndView("settings");

        User user = userService.getById(userData.getUserId());

        CompanyFormulaResponse formula = formulaClient.getFormula(userData.getUserId()).getBody();


        EditProfileRequest editProfileRequest = DtoMapper.from(user);

        modelAndView.addObject("currentPage", "settings");
        modelAndView.addObject("user", user);
        modelAndView.addObject("formula", formula);
        modelAndView.addObject("editProfileRequest", editProfileRequest);

        return modelAndView;
    }

    @PostMapping("/update-profile")
    public ModelAndView updateProfile(@AuthenticationPrincipal UserData userData, @Valid EditProfileRequest editProfileRequest, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView("settings");

        User user = userService.getById(userData.getUserId());

        try {
            userService.updateProfile(user, editProfileRequest);
            modelAndView.addObject("user", user);
        } catch (EmailAlreadyExists e) {
            bindingResult.rejectValue("email", "error.email", e.getMessage());

            modelAndView.addObject("user", user);
            modelAndView.addObject("editProfileRequest", editProfileRequest);
            modelAndView.addObject("formula", formulaClient.getFormula(userData.getUserId()).getBody());

            return modelAndView;
        }

        return new ModelAndView("redirect:/settings");
    }

    @PostMapping("/update-formula")
    public String updateFormula(@AuthenticationPrincipal UserData userData, @ModelAttribute CompanyFormulaRequest request) {
        formulaClient.updateFormula(formulaConfig.getKey(), userData.getUserId(), request);

        return "redirect:/settings";
    }

}
