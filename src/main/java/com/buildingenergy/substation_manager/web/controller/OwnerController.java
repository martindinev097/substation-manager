package com.buildingenergy.substation_manager.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/owners")
public class OwnerController {

    @GetMapping
    public ModelAndView getOwnersPage() {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("owners");
        modelAndView.addObject("currentPage", "owners");

        return modelAndView;
    }

}
