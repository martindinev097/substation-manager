package com.buildingenergy.substation_manager.web.controller;

import com.buildingenergy.substation_manager.company.model.Company;
import com.buildingenergy.substation_manager.company.service.CompanyService;
import com.buildingenergy.substation_manager.exception.EmailAlreadyExists;
import com.buildingenergy.substation_manager.floor.service.FloorService;
import com.buildingenergy.substation_manager.meter.service.MeterService;
import com.buildingenergy.substation_manager.security.UserData;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.user.service.UserService;
import com.buildingenergy.substation_manager.util.LoginPageUtil;
import com.buildingenergy.substation_manager.web.dto.LoginRequest;
import com.buildingenergy.substation_manager.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class IndexController {

    private final UserService userService;
    private final CompanyService companyService;
    private final FloorService floorService;
    private final MeterService meterService;

    public IndexController(UserService userService, CompanyService companyService, FloorService floorService, MeterService meterService) {
        this.userService = userService;
        this.companyService = companyService;
        this.floorService = floorService;
        this.meterService = meterService;
    }

    @GetMapping("/")
    public String indexPage() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage(
            @RequestParam(name = "error", required = false) String errorMessage,
            @RequestParam(name = "account-inactive", required = false) String inactiveMessage,
            @RequestParam(name = "username-not-exist", required = false) String usernameNotExist,
            @RequestParam(name = "registered", required = false) String registered) {

        ModelAndView modelAndView = new ModelAndView("login");

        modelAndView.addObject("loginRequest", new LoginRequest());

        LoginPageUtil.addMessages(modelAndView, errorMessage, inactiveMessage, usernameNotExist, registered);

        return modelAndView;
    }

    @GetMapping("/register")
    public ModelAndView getRegisterPage() {
        ModelAndView modelAndView = new ModelAndView("register");

        modelAndView.addObject("registerRequest", new RegisterRequest());

        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView register(@Valid RegisterRequest registerRequest, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView("register");

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("register");
            return modelAndView;
        }

        userService.register(registerRequest);

        modelAndView.setViewName("redirect:/login?registered");

        return modelAndView;
    }

    @GetMapping("/home")
    public ModelAndView getHomePage(@AuthenticationPrincipal UserData userData) {
        ModelAndView modelAndView = new ModelAndView();

        User user = userService.getById(userData.getUserId());
        List<Company> companies = companyService.findTop5ByUser(user);
        int companyCount = companyService.findAllByUser(user).size();
        int floorCount = floorService.findAllByUser(user).size();
        int meterCount = meterService.findAllByUser(user).size();

        modelAndView.setViewName("home");
        modelAndView.addObject("user", user);
        modelAndView.addObject("currentPage", "home");
        modelAndView.addObject("companies", companies);
        modelAndView.addObject("floorCount", floorCount);
        modelAndView.addObject("meterCount", meterCount);
        modelAndView.addObject("companyCount", companyCount);

        return modelAndView;
    }

    @ExceptionHandler(EmailAlreadyExists.class)
    public ModelAndView handleEmailAlreadyExists(EmailAlreadyExists ex) {
        ModelAndView modelAndView = new ModelAndView("register");

        modelAndView.addObject("registerRequest", new RegisterRequest());
        modelAndView.addObject("emailExistsMessage", ex.getMessage());

        return modelAndView;
    }
}
