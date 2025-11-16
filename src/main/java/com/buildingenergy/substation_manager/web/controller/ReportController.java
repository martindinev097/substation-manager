package com.buildingenergy.substation_manager.web.controller;

import com.buildingenergy.substation_manager.meter.model.MeterHistory;
import com.buildingenergy.substation_manager.meter.service.MeterHistoryService;
import com.buildingenergy.substation_manager.reading.model.ReadingHistory;
import com.buildingenergy.substation_manager.reading.service.ReadingHistoryService;
import com.buildingenergy.substation_manager.report.service.ExcelExportService;
import com.buildingenergy.substation_manager.security.UserData;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReadingHistoryService readingHistoryService;
    private final MeterHistoryService meterHistoryService;
    private final ExcelExportService excelExportService;
    private final UserService userService;

    public ReportController(ReadingHistoryService readingHistoryService, MeterHistoryService meterHistoryService, ExcelExportService excelExportService, UserService userService) {
        this.readingHistoryService = readingHistoryService;
        this.meterHistoryService = meterHistoryService;
        this.excelExportService = excelExportService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView getReportsPage(@RequestParam Optional<Integer> month, @AuthenticationPrincipal UserData userData) {
        ModelAndView modelAndView = new ModelAndView();

        User user = userService.getById(userData.getUserId());

        int selectedMonth = month.orElse(LocalDate.now().getMonthValue());

        modelAndView.setViewName("reports");
        modelAndView.addObject("currentPage", "reports");
        modelAndView.addObject("readingHistory", readingHistoryService.getAllByMonthAndUser(selectedMonth, user));
        modelAndView.addObject("selectedMonth", selectedMonth);
        modelAndView.addObject("meterHistory", meterHistoryService.getAllByMonthAndUser(selectedMonth, user));

        return modelAndView;
    }

    @GetMapping("/company/export")
    public void exportToExcel(@RequestParam int month, HttpServletResponse response, @AuthenticationPrincipal UserData userData) throws IOException {
        User user = userService.getById(userData.getUserId());

        List<ReadingHistory> historyList = readingHistoryService.getAllByMonthAndUser(month, user);

        excelExportService.exportReadingHistory(historyList, response);
    }

    @GetMapping("/meters/export")
    public void exportMetersToExcel(@RequestParam(required = false) int month, HttpServletResponse response, @AuthenticationPrincipal UserData userData) throws IOException {
        User user = userService.getById(userData.getUserId());

        List<MeterHistory> historyList = meterHistoryService.getAllByMonthAndUser(month, user);

        excelExportService.exportMeterHistory(historyList, response);
    }

}
