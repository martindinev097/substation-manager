package com.buildingenergy.substation_manager.web.controller;

import com.buildingenergy.substation_manager.floor.model.Floor;
import com.buildingenergy.substation_manager.floor.service.FloorService;
import com.buildingenergy.substation_manager.formula.dto.CompanyFormulaResponse;
import com.buildingenergy.substation_manager.formula.service.FormulaService;
import com.buildingenergy.substation_manager.reading.service.ReadingService;
import com.buildingenergy.substation_manager.security.UserData;
import com.buildingenergy.substation_manager.company.model.Company;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.company.service.CompanyService;
import com.buildingenergy.substation_manager.user.service.UserService;
import com.buildingenergy.substation_manager.web.dto.ReadingListWrapper;
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
    private final FormulaService formulaService;
    private final FloorService floorService;

    public FloorController(UserService userService, CompanyService companyService, ReadingService readingService, FormulaService formulaService, FloorService floorService) {
        this.userService = userService;
        this.companyService = companyService;
        this.readingService = readingService;
        this.formulaService = formulaService;
        this.floorService = floorService;
    }

    @GetMapping("/{floorNumber}")
    public ModelAndView getFloorPage(@PathVariable int floorNumber, @AuthenticationPrincipal UserData userData) {
        ModelAndView modelAndView = new ModelAndView();

        User user = userService.getById(userData.getUserId());

        Floor floor = floorService.findByFloorNumberAndUser(floorNumber, user);

        List<Company> companies = companyService.findAllByFloorAndUser(floorNumber, user);

        ReadingListWrapper wrapper = readingService.getWrapperForCompanies(companies);

        CompanyFormulaResponse formula = formulaService.getCompanyFormula(userData.getUserId());

        boolean areSwapped = readingService.areSwapped(user, floor);

        modelAndView.setViewName("floor");
        modelAndView.addObject("floorNumber", floorNumber);
        modelAndView.addObject("currentPage", "floor");
        modelAndView.addObject("companies", companies);
        modelAndView.addObject("formula", formula);
        modelAndView.addObject("readingWrapper", wrapper);
        modelAndView.addObject("areSwapped", areSwapped);

        return modelAndView;
    }

    @PostMapping("/{floor}/add-company")
    public String addCompany(@PathVariable int floor, @RequestParam(name = "companyName") String companyName, @AuthenticationPrincipal UserData userData) {
        User user = userService.getById(userData.getUserId());

        companyService.addCompanyForFloor(companyName, floor, user);

        return "redirect:/floor/" + floor;
    }

}
