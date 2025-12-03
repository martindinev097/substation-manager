package com.buildingenergy.substation_manager.web.controller;

import com.buildingenergy.substation_manager.export.model.ExportHistory;
import com.buildingenergy.substation_manager.export.repository.ExportHistoryRepository;
import com.buildingenergy.substation_manager.security.UserData;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/export")
public class ExportHistoryController {

    private final ExportHistoryRepository exportHistoryRepository;

    public ExportHistoryController(ExportHistoryRepository exportHistoryRepository) {
        this.exportHistoryRepository = exportHistoryRepository;
    }

    @GetMapping("/history")
    public String viewExportHistory(@AuthenticationPrincipal UserData userData, Model model) {
        List<ExportHistory> history = exportHistoryRepository.findAllByUserIdOrderByExportedAtDesc(userData.getUserId());

        model.addAttribute("currentPage", "export-history");
        model.addAttribute("history", history);

        return "export-history";
    }

}
