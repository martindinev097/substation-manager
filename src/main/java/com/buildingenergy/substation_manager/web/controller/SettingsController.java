package com.buildingenergy.substation_manager.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/settings")
public class SettingsController {

    @GetMapping
    public ModelAndView getSettingsPage() {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("settings");
        modelAndView.addObject("currentPage", "settings");

        return modelAndView;
    }

}
