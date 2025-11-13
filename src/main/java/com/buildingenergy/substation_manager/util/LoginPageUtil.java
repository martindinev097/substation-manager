package com.buildingenergy.substation_manager.util;

import lombok.experimental.UtilityClass;
import org.springframework.web.servlet.ModelAndView;

@UtilityClass
public class LoginPageUtil {

    public void addMessages(ModelAndView modelAndView, String errorMessage, String inactiveMessage, String usernameNotExist, String registered) {
        if (errorMessage != null) {
            modelAndView.addObject("errorMessage", "Invalid username or password.");
        }

        if (inactiveMessage != null) {
            modelAndView.addObject("inactiveMessage", "Your account has been deactivated by an administrator.");
        }

        if (usernameNotExist != null) {
            modelAndView.addObject("usernameNotExist", "Username does not exist.");
        }

        if (registered != null) {
            modelAndView.addObject("registeredMessage", "Registration successful. You can now log in.");
        }
    }

}
