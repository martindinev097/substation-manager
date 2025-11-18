package com.buildingenergy.substation_manager.web.controller;

import com.buildingenergy.substation_manager.company.service.CompanyService;
import com.buildingenergy.substation_manager.exception.CompanyNotFound;
import com.buildingenergy.substation_manager.security.UserData;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.user.service.UserService;
import com.buildingenergy.substation_manager.web.dto.CompanyView;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/owners")
public class OwnerController {

    private final CompanyService companyService;
    private final UserService userService;

    public OwnerController(CompanyService companyService, UserService userService) {
        this.companyService = companyService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView getOwnersPage(@AuthenticationPrincipal UserData userData) {
        ModelAndView modelAndView = new ModelAndView();

        User user = userService.getById(userData.getUserId());

        List<CompanyView> companies = companyService.getAllWithTotalConsumption(user);

        modelAndView.setViewName("owners");

        modelAndView.addObject("currentPage", "owners");
        modelAndView.addObject("companies", companies);

        return modelAndView;
    }

    @DeleteMapping("/delete/{id}")
    public String deleteCompany(@PathVariable UUID id, @AuthenticationPrincipal UserData userData, RedirectAttributes redirectAttributes) {
        companyService.deleteCompany(id, userData.getUserId());

        redirectAttributes.addFlashAttribute("successMessage", "Company deleted successfully.");

        return "redirect:/owners";
    }

    @ExceptionHandler(CompanyNotFound.class)
    public String handleCompanyNotFound(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete company.");

        return "redirect:/owners";
    }

}
