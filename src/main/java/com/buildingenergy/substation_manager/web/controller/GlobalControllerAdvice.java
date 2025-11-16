package com.buildingenergy.substation_manager.web.controller;

import com.buildingenergy.substation_manager.exception.*;
import com.buildingenergy.substation_manager.web.dto.RegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(CannotChangeAdminStatus.class)
    public String handleCannotChangeAdminStatus(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "You cannot deactivate another admin account!");

        return "redirect:/admin-panel";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CompanyNotFound.class)
    public String handleCompanyNotFound(CompanyNotFound ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());

        return "not-found";
    }

    @ExceptionHandler(ForbiddenAccess.class)
    public String handleForbiddenAccess() {
        return "redirect:/logout?roleChanged=true";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UsernameDoesNotExist.class)
    public String handleUsernameDoesNotExist(UsernameDoesNotExist ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());

        return "not-found";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(FloorNotFound.class)
    public String handleFloorNotFound(FloorNotFound ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());

        return "not-found";
    }

    @ExceptionHandler(UsernameAlreadyExists.class)
    public ModelAndView handleUsernameAlreadyExists(UsernameAlreadyExists ex) {
        ModelAndView modelAndView = new ModelAndView("register");

        modelAndView.addObject("usernameExistsMessage", ex.getMessage());
        modelAndView.addObject("registerRequest", new RegisterRequest());

        return modelAndView;
    }

    @ExceptionHandler(PasswordsDoNotMatch.class)
    public ModelAndView handlePasswordsDoNotMatch(PasswordsDoNotMatch ex) {
        ModelAndView modelAndView = new ModelAndView("register");

        modelAndView.addObject("registerRequest", new RegisterRequest());
        modelAndView.addObject("passwordErrorMessage", ex.getMessage());

        return modelAndView;
    }

    @ExceptionHandler({
            NoResourceFoundException.class,
            AuthorizationDeniedException.class,
            MethodArgumentNotValidException.class
    })
    public String handleNoResourceFoundException() {
        return "not-found";
    }

    @ExceptionHandler(Exception.class)
    public String handleAllOtherExceptions() {
        return "internal-server-error";
    }

}
