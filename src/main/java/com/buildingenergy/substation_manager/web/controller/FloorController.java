package com.buildingenergy.substation_manager.web.controller;

import com.buildingenergy.substation_manager.security.UserData;
import com.buildingenergy.substation_manager.user.model.Company;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.user.service.CompanyService;
import com.buildingenergy.substation_manager.user.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/floor")
public class FloorController {

    private final UserService userService;
    private final CompanyService companyService;

    public FloorController(UserService userService, CompanyService companyService) {
        this.userService = userService;
        this.companyService = companyService;
    }

    @GetMapping("/{floor}")
    public ModelAndView getFloorPage(@PathVariable int floor, @AuthenticationPrincipal UserData userData) {
        ModelAndView modelAndView = new ModelAndView();

        User user = userService.getById(userData.getUserId());

        List<Company> companies = companyService.findAllByFloorAndUser(floor, user);

        modelAndView.setViewName("floor");
        modelAndView.addObject("floorNumber", floor);
        modelAndView.addObject("currentPage", "floor");
        modelAndView.addObject("companies", companies);

        return modelAndView;
    }

    @PostMapping("/{floor}/add-company")
    public ModelAndView addCompany(@PathVariable int floor, @RequestParam(name = "companyName") String companyName, @AuthenticationPrincipal UserData userData) {
        User user = userService.getById(userData.getUserId());

        companyService.addCompanyForFloor(companyName, floor, user);

        return new ModelAndView("redirect:/floor/" + floor);
    }

}
