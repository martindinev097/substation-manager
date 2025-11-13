package com.buildingenergy.substation_manager.web.controller;

import com.buildingenergy.substation_manager.exception.CannotChangeAdminStatus;
import com.buildingenergy.substation_manager.exception.ForbiddenAccess;
import com.buildingenergy.substation_manager.security.UserData;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.user.model.UserRole;
import com.buildingenergy.substation_manager.user.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @PostMapping("/deactivate/{id}")
    public String deactivateUser(@PathVariable UUID id) {
        User user = userService.getById(id);

        try {
            userService.changeStatus(user);
        } catch (CannotChangeAdminStatus e) {
            return "redirect:/admin-panel?error=cannot-deactivate-admin";
        }

        return "redirect:/admin-panel";
    }

    @PostMapping("/change-role/{id}")
    public String changeRole(@PathVariable UUID id, @AuthenticationPrincipal UserData userData) {
        User currentUser = userService.getById(userData.getUserId());

        try {
            userService.updateRole(id, currentUser);
        } catch (ForbiddenAccess e) {
            return "redirect:/logout?roleChanged=true";
        }

        return "redirect:/admin-panel";
    }


}
