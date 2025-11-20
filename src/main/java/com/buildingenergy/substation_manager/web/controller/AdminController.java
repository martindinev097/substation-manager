package com.buildingenergy.substation_manager.web.controller;

import com.buildingenergy.substation_manager.security.UserData;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.user.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin-panel")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView getAdminPanel() {
        ModelAndView modelAndView = new ModelAndView("admin-panel");

        List<User> users = userService.findAll();
        List<User> admins = userService.findAllAdmins();

        modelAndView.addObject("users", users);
        modelAndView.addObject("admins", admins);
        modelAndView.addObject("currentPage", "admin");


        return modelAndView;
    }

    @PatchMapping("/deactivate/{id}")
    public String deactivateUser(@PathVariable UUID id, @AuthenticationPrincipal UserData userData) {
        User user = userService.getById(id);
        User admin = userService.getById(userData.getUserId());

        userService.changeStatus(user, admin);

        return "redirect:/admin-panel";
    }

    @PatchMapping("/role/change/{id}")
    public String changeRole(@PathVariable UUID id, @AuthenticationPrincipal UserData userData) {
        User currentUser = userService.getById(userData.getUserId());

        userService.updateRole(id, currentUser);

        return "redirect:/admin-panel";
    }


}
