package com.buildingenergy.substation_manager.web.controller;

import com.buildingenergy.substation_manager.meter.model.MeterHistory;
import com.buildingenergy.substation_manager.meter.service.MeterHistoryService;
import com.buildingenergy.substation_manager.reading.model.ReadingHistory;
import com.buildingenergy.substation_manager.reading.service.ReadingHistoryService;
import com.buildingenergy.substation_manager.report.service.ExcelExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReadingHistoryService readingHistoryService;
    private final MeterHistoryService meterHistoryService;
    private final ExcelExportService excelExportService;

    public ReportController(ReadingHistoryService readingHistoryService, MeterHistoryService meterHistoryService, ExcelExportService excelExportService) {
        this.readingHistoryService = readingHistoryService;
        this.meterHistoryService = meterHistoryService;
        this.excelExportService = excelExportService;
    }

    @GetMapping
    public ModelAndView getReportsPage(@RequestParam(name = "month", required = false) Integer month) {
        ModelAndView modelAndView = new ModelAndView();

        if (month == null) month = LocalDate.now().getMonthValue();

        modelAndView.setViewName("reports");
        modelAndView.addObject("currentPage", "reports");
        modelAndView.addObject("readingHistory", readingHistoryService.getAllByMonth(month));
        modelAndView.addObject("selectedMonth", month);
        modelAndView.addObject("meterHistory", meterHistoryService.getAllByMonth(month));

        return modelAndView;
    }

    @GetMapping("/export-company")
    public void exportToExcel(@RequestParam(required = false) Integer month, HttpServletResponse response) throws IOException {
        if (month == null) month = LocalDate.now().getMonthValue();

        List<ReadingHistory> historyList = readingHistoryService.getAllByMonth(month);

        excelExportService.exportReadingHistory(historyList, response);
    }

    @GetMapping("/export-meters")
    public void exportMetersToExcel(@RequestParam(required = false) Integer month, HttpServletResponse response) throws IOException {
        if (month == null) month = LocalDate.now().getMonthValue();

        List<MeterHistory> historyList = meterHistoryService.getAllByMonth(month);

        excelExportService.exportMeterHistory(historyList, response);
    }

}
