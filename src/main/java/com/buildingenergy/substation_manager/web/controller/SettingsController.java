package com.buildingenergy.substation_manager.web.controller;

import com.buildingenergy.substation_manager.exception.EmailAlreadyExists;
import com.buildingenergy.substation_manager.formula.dto.CompanyFormulaRequest;
import com.buildingenergy.substation_manager.formula.dto.MeterFormulaRequest;
import com.buildingenergy.substation_manager.formula.service.FormulaService;
import com.buildingenergy.substation_manager.security.UserData;
import com.buildingenergy.substation_manager.settings.SettingsPageModelBuilder;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/settings")
public class SettingsController {

    private final UserService userService;
    private final FormulaService formulaService;
    private final SettingsPageModelBuilder modelBuilder;

    public SettingsController(UserService userService, FormulaService companyFormulaService, SettingsPageModelBuilder modelBuilder) {
        this.userService = userService;
        this.formulaService = companyFormulaService;
        this.modelBuilder = modelBuilder;
    }

    @GetMapping
    public ModelAndView getSettingsPage(@AuthenticationPrincipal UserData userData, @RequestParam(name = "tab", required = false, defaultValue = "profile") String activeTab) {
        ModelAndView modelAndView = new ModelAndView("settings");

        User user = userService.getById(userData.getUserId());
        EditProfileRequest editProfileRequest = DtoMapper.from(user);

        modelBuilder.build(modelAndView, user, editProfileRequest, activeTab);

        return modelAndView;
    }

    @PutMapping("/profile/update")
    public ModelAndView updateProfile(@AuthenticationPrincipal UserData userData, @Valid EditProfileRequest editProfileRequest, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView = new ModelAndView("settings");

        User user = userService.getById(userData.getUserId());

        if (bindingResult.hasErrors()) {
            modelBuilder.build(modelAndView, user, editProfileRequest, "profile");

            return modelAndView;
        }

        userService.updateProfile(user, editProfileRequest);

        redirectAttributes.addFlashAttribute("successMessage", "Successfully updated profile.");

        return new ModelAndView("redirect:/settings");
    }

    @PutMapping("/company/formula/update")
    public String updateCompanyFormula(@AuthenticationPrincipal UserData userData,
                                @ModelAttribute @Valid CompanyFormulaRequest request,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorCompanyFormulaMessage", "Formula fields must not be blank or negative numbers.");
            return "redirect:/settings?tab=settings";
        }

        boolean success = formulaService.updateCompanyFormula(userData.getUserId(), request);

        redirectAttributes.addFlashAttribute("successUpdateMessage", success);

        return "redirect:/settings?tab=settings";
    }

    @PutMapping("/meter/formula/update")
    public String updateMeterFormula(@AuthenticationPrincipal UserData userData,
                                     @ModelAttribute @Valid MeterFormulaRequest request,
                                     BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMeterFormulaMessage", "Formula fields must not be blank or negative numbers.");
            return "redirect:/settings?tab=settings";
        }

        boolean success = formulaService.updateMeterFormula(userData.getUserId(), request);

        redirectAttributes.addFlashAttribute("meterSuccess", success);

        return "redirect:/settings?tab=settings";
    }

    @ExceptionHandler(EmailAlreadyExists.class)
    public ModelAndView handleEmailAlreadyExists(EmailAlreadyExists ex, @AuthenticationPrincipal UserData userData) {
        ModelAndView modelAndView = new ModelAndView("settings");

        User user = userService.getById(userData.getUserId());
        EditProfileRequest editProfileRequest = DtoMapper.from(user);

        modelBuilder.build(modelAndView, user, editProfileRequest, "profile");

        modelAndView.addObject("emailExistsMessage", ex.getMessage());

        return modelAndView;
    }

}
