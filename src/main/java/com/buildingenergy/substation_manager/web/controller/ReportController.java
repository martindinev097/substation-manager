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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    public String getReportsPage(@RequestParam Optional<Integer> month,
                                       @AuthenticationPrincipal UserData userData,
                                       Model model) {
        User user = userService.getById(userData.getUserId());

        int selectedMonth = month.orElse(LocalDate.now().getMonthValue());

        model.addAttribute("currentPage", "reports");
        model.addAttribute("readingHistory", readingHistoryService.getAllByMonthAndUser(selectedMonth, user));
        model.addAttribute("selectedMonth", selectedMonth);
        model.addAttribute("meterHistory", meterHistoryService.getAllByMonthAndUser(selectedMonth, user));

        return "reports";
    }

    @GetMapping("/company/export")
    public void exportToExcel(@RequestParam int month, HttpServletResponse response, @AuthenticationPrincipal UserData userData) throws IOException {
        User user = userService.getById(userData.getUserId());

        List<ReadingHistory> historyList = readingHistoryService.getAllByMonthAndUser(month, user);

        excelExportService.exportReadingHistory(historyList, response, month);
    }

    @GetMapping("/meters/export")
    public void exportMetersToExcel(@RequestParam(required = false) int month, HttpServletResponse response, @AuthenticationPrincipal UserData userData) throws IOException {
        User user = userService.getById(userData.getUserId());

        List<MeterHistory> historyList = meterHistoryService.getAllByMonthAndUser(month, user);

        excelExportService.exportMeterHistory(historyList, response, month);
    }

    @DeleteMapping("/company/delete/{readingId}/{month}")
    public String deleteCompany(@PathVariable UUID readingId, @PathVariable int month, RedirectAttributes redirectAttributes) {
        readingHistoryService.deleteCompanyByIdAndMonth(readingId, month);

        redirectAttributes.addFlashAttribute("deletedMessage", "Successfully deleted company.");

        return "redirect:/reports?month=" + month;
    }

    @DeleteMapping("/meter/delete/{meterId}/{month}")
    public String deleteMeter(@PathVariable UUID meterId, @PathVariable int month, RedirectAttributes redirectAttributes) {
        meterHistoryService.deleteMeterByIdAndMonth(meterId, month);

        redirectAttributes.addFlashAttribute("deletedMeterMessage", "Successfully deleted meter.");

        return "redirect:/reports?month=" + month;
    }

}
