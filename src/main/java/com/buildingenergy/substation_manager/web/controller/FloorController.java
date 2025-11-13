package com.buildingenergy.substation_manager.web.controller;

import com.buildingenergy.substation_manager.formula.client.CompanyFormulaClient;
import com.buildingenergy.substation_manager.formula.dto.CompanyFormulaResponse;
import com.buildingenergy.substation_manager.reading.service.ReadingService;
import com.buildingenergy.substation_manager.security.UserData;
import com.buildingenergy.substation_manager.company.model.Company;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.company.service.CompanyService;
import com.buildingenergy.substation_manager.user.service.UserService;
import com.buildingenergy.substation_manager.web.dto.ReadingListWrapper;
import com.buildingenergy.substation_manager.web.dto.ReadingRequest;
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
    private final ReadingService readingService;
    private final CompanyFormulaClient formulaClient;

    public FloorController(UserService userService, CompanyService companyService, ReadingService readingService, CompanyFormulaClient formulaClient) {
        this.userService = userService;
        this.companyService = companyService;
        this.readingService = readingService;
        this.formulaClient = formulaClient;
    }

    @GetMapping("/{floorNumber}")
    public ModelAndView getFloorPage(@PathVariable int floorNumber, @AuthenticationPrincipal UserData userData) {
        ModelAndView modelAndView = new ModelAndView();

        User user = userService.getById(userData.getUserId());

        List<Company> companies = companyService.findAllByFloorAndUser(floorNumber, user);

        List<ReadingRequest> readingRequests = readingService.getReadingRequests(companies);

        ReadingListWrapper wrapper = new ReadingListWrapper();
        wrapper.setReadings(readingRequests);

        CompanyFormulaResponse formula = formulaClient.getFormula(userData.getUserId()).getBody();

        modelAndView.setViewName("floor");
        modelAndView.addObject("floorNumber", floorNumber);
        modelAndView.addObject("currentPage", "floor");
        modelAndView.addObject("companies", companies);
        modelAndView.addObject("formula", formula);
        modelAndView.addObject("readingWrapper", wrapper);

        return modelAndView;
    }

    @PostMapping("/{floor}/add-company")
    public ModelAndView addCompany(@PathVariable int floor, @RequestParam(name = "companyName") String companyName, @AuthenticationPrincipal UserData userData) {
        User user = userService.getById(userData.getUserId());

        companyService.addCompanyForFloor(companyName, floor, user);

        return new ModelAndView("redirect:/floor/" + floor);
    }

}
