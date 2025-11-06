package com.buildingenergy.substation_manager.web.controller;

import com.buildingenergy.substation_manager.meter.service.MeterHistoryService;
import com.buildingenergy.substation_manager.reading.service.ReadingHistoryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReadingHistoryService readingHistoryService;
    private final MeterHistoryService meterHistoryService;

    public ReportController(ReadingHistoryService readingHistoryService, MeterHistoryService meterHistoryService) {
        this.readingHistoryService = readingHistoryService;
        this.meterHistoryService = meterHistoryService;
    }

    @GetMapping
    public ModelAndView getReportsPage() {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("reports");
        modelAndView.addObject("currentPage", "reports");
        modelAndView.addObject("readingHistory", readingHistoryService.getAll());
        modelAndView.addObject("meterHistory", meterHistoryService.getAll());

        return modelAndView;
    }

}
