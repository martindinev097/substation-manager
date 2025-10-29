package com.buildingenergy.substation_manager.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FloorController {

    @GetMapping("/floor/{floor}")
    public ModelAndView getFloorPage(@PathVariable int floor) {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("floor");
        modelAndView.addObject("floorNumber", floor);
        modelAndView.addObject("currentPage", "floor");

        return modelAndView;
    }

}
