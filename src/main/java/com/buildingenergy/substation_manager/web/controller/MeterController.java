package com.buildingenergy.substation_manager.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/meters")
public class MeterController {

    @GetMapping("/floor/{floor}")
    public ModelAndView getMetersPage(@PathVariable int floor) {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("meters");
        modelAndView.addObject("floorNumber", floor);
        modelAndView.addObject("currentPage", "meters");

        return modelAndView;
    }

}
