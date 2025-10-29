package com.buildingenergy.substation_manager.web.controller;

import com.buildingenergy.substation_manager.exception.EmailAlreadyExists;
import com.buildingenergy.substation_manager.exception.PasswordsDoNotMatch;
import com.buildingenergy.substation_manager.exception.UsernameAlreadyExists;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {

    private final UserService userService;

    public IndexController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String indexPage() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage(
            @RequestParam(name = "error", required = false) String errorMessage,
            @RequestParam(name = "registered", required = false) String registered) {

        ModelAndView modelAndView = new ModelAndView("login");

        modelAndView.addObject("loginRequest", new LoginRequest());

        LoginPageUtil.addMessages(modelAndView, errorMessage, registered);

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

        try {
            userService.register(registerRequest);

            modelAndView.setViewName("redirect:/login?registered");
        } catch (UsernameAlreadyExists e) {
            bindingResult.rejectValue("username", "error.username", e.getMessage());
            return modelAndView;
        } catch (EmailAlreadyExists e) {
            bindingResult.rejectValue("email", "error.email", e.getMessage());
            return modelAndView;
        } catch (PasswordsDoNotMatch e) {
            bindingResult.rejectValue("confirmedPassword", "error.confirmedPassword", e.getMessage());
            return modelAndView;
        }

        return modelAndView;
    }

    @GetMapping("/home")
    public ModelAndView getHomePage(@AuthenticationPrincipal UserData userData) {
        ModelAndView modelAndView = new ModelAndView();

        User user = userService.getById(userData.getUserId());

        modelAndView.setViewName("home");
        modelAndView.addObject("user", user);
        modelAndView.addObject("currentPage", "home");

        return modelAndView;
    }
}
