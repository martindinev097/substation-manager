package com.buildingenergy.substation_manager.web.controller;

import com.buildingenergy.substation_manager.company.model.Company;
import com.buildingenergy.substation_manager.company.service.CompanyService;
import com.buildingenergy.substation_manager.floor.model.Floor;
import com.buildingenergy.substation_manager.floor.service.FloorService;
import com.buildingenergy.substation_manager.reading.service.ReadingHistoryService;
import com.buildingenergy.substation_manager.reading.service.ReadingService;
import com.buildingenergy.substation_manager.security.UserData;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.user.service.UserService;
import com.buildingenergy.substation_manager.web.dto.ReadingListWrapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/readings")
public class ReadingController {

    private final ReadingService readingService;
    private final UserService userService;
    private final FloorService floorService;
    private final CompanyService companyService;
    private final ReadingHistoryService readingHistoryService;

    public ReadingController(ReadingService readingService, UserService userService, FloorService floorService, CompanyService companyService, ReadingHistoryService readingHistoryService) {
        this.readingService = readingService;
        this.userService = userService;
        this.floorService = floorService;
        this.companyService = companyService;
        this.readingHistoryService = readingHistoryService;
    }

    @PostMapping("/save")
    public String saveAllReadings(@RequestParam int floorNumber, @ModelAttribute("readingWrapper") ReadingListWrapper wrapper, @AuthenticationPrincipal UserData userData) {
        readingService.updateAllReadings(wrapper);

        User user = userService.getById(userData.getUserId());
        List<Company> companies = companyService.findAllByFloorAndUser(floorNumber, user);
        readingHistoryService.backupCurrentReadings(companies);

        return "redirect:/floor/" + floorNumber;
    }

    @PostMapping("/swap")
    public String swapReadings(@RequestParam int floorNumber, @AuthenticationPrincipal UserData userData) {
        User user = userService.getById(userData.getUserId());

        Floor floor = floorService.findByFloorNumberAndUser(floorNumber, user);

        readingService.swapAllReadingsForFloor(floor, user);

        return "redirect:/floor/" + floorNumber;
    }

}
