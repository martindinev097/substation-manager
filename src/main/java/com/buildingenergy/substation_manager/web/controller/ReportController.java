package com.buildingenergy.substation_manager.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/reports")
public class ReportsController {

    @GetMapping
    public ModelAndView getReportsPage() {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("reports");
        modelAndView.addObject("currentPage", "reports");

        return modelAndView;
    }

}
