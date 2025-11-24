package com.buildingenergy.substation_manager.web.controller;

import com.buildingenergy.substation_manager.formula.dto.MeterFormulaResponse;
import com.buildingenergy.substation_manager.formula.service.FormulaService;
import com.buildingenergy.substation_manager.meter.model.Meter;
import com.buildingenergy.substation_manager.meter.service.MeterHistoryService;
import com.buildingenergy.substation_manager.meter.service.MeterService;
import com.buildingenergy.substation_manager.floor.model.Floor;
import com.buildingenergy.substation_manager.floor.service.FloorService;
import com.buildingenergy.substation_manager.security.UserData;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.user.service.UserService;
import com.buildingenergy.substation_manager.web.dto.MeterReadingWrapper;
import com.buildingenergy.substation_manager.web.dto.MeterRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/meters")
public class MeterController {

    private final UserService userService;
    private final FloorService floorService;
    private final MeterService meterService;
    private final MeterHistoryService meterHistoryService;
    private final FormulaService formulaService;

    public MeterController(UserService userService, FloorService floorService, MeterService meterService, MeterHistoryService meterHistoryService, FormulaService formulaService) {
        this.userService = userService;
        this.floorService = floorService;
        this.meterService = meterService;
        this.meterHistoryService = meterHistoryService;
        this.formulaService = formulaService;
    }

    @GetMapping("/floor/{floorNumber}")
    public ModelAndView getMetersPage(@PathVariable int floorNumber, @AuthenticationPrincipal UserData userData) {
        ModelAndView modelAndView = new ModelAndView();

        User user = userService.getById(userData.getUserId());
        Floor floor = floorService.findByFloorNumberAndUser(floorNumber, user);
        List<Meter> meters = meterService.findAllByFloorAndUser(floor, user);
        MeterFormulaResponse meterFormula = formulaService.getMeterFormula(user.getId());

        MeterReadingWrapper wrapper = meterService.buildMeterReadingWrapper(meters);

        boolean areSwapped = meterService.areSwapped(user, floor);

        modelAndView.setViewName("meters");
        modelAndView.addObject("floorNumber", floorNumber);
        modelAndView.addObject("currentPage", "meters");
        modelAndView.addObject("meters", meters);
        modelAndView.addObject("meterFormula", meterFormula);
        modelAndView.addObject("meterRequest", new MeterRequest());
        modelAndView.addObject("readingWrapper", wrapper);
        modelAndView.addObject("areSwapped", areSwapped);

        return modelAndView;
    }

    @PostMapping("/floor/{floorNumber}")
    public ModelAndView addMeter(@PathVariable int floorNumber, @Valid MeterRequest meterRequest, BindingResult bindingResult, @AuthenticationPrincipal UserData userData) {
        ModelAndView modelAndView = new ModelAndView();

        User user = userService.getById(userData.getUserId());
        Floor floor = floorService.findByFloorNumberAndUser(floorNumber, user);
        List<Meter> meters = meterService.findAllByFloorAndUser(floor, user);

        if (bindingResult.hasErrors()) {
            MeterReadingWrapper wrapper = meterService.buildMeterReadingWrapper(meters);
            MeterFormulaResponse meterFormula = formulaService.getMeterFormula(user.getId());

            modelAndView.addObject("errorMessage", "Invalid data. Please check your input");
            modelAndView.addObject("floorNumber", floorNumber);
            modelAndView.addObject("currentPage", "meters");
            modelAndView.addObject("meters", meters);
            modelAndView.addObject("readingWrapper", wrapper);
            modelAndView.addObject("meterFormula", meterFormula);

            modelAndView.setViewName("meters");

            return modelAndView;
        }

        meterService.createMeter(meterRequest, floor, floorNumber, user);

        modelAndView.setViewName("redirect:/meters/floor/" + floorNumber);

        return modelAndView;
    }

    @PostMapping("/save")
    public ModelAndView saveMeterReadings(@RequestParam int floorNumber, @ModelAttribute("readingWrapper") MeterReadingWrapper wrapper, @AuthenticationPrincipal UserData userData) {
        ModelAndView modelAndView = new ModelAndView();

        User user = userService.getById(userData.getUserId());
        Floor floor = floorService.findByFloorNumberAndUser(floorNumber, user);

        meterService.updateMeterReadings(wrapper.getReadings(), user, floor);

        List<Meter> meters = meterService.findAllByFloorAndUser(floor, user);

        meterHistoryService.backupCurrentReadings(meters, user);

        modelAndView.setViewName("redirect:/meters/floor/" + floorNumber);

        return modelAndView;
    }

    @PostMapping("/swap")
    public ModelAndView swapMeterReadings(@RequestParam int floorNumber, @AuthenticationPrincipal UserData userData) {
        ModelAndView modelAndView = new ModelAndView();

        User user = userService.getById(userData.getUserId());
        Floor floor = floorService.findByFloorNumberAndUser(floorNumber, user);

        List<Meter> meters = meterService.findAllByFloorAndUser(floor, user);

        meterService.swapMeterReadings(meters);

        modelAndView.setViewName("redirect:/meters/floor/" + floorNumber);

        return modelAndView;
    }

}
