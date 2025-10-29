package com.buildingenergy.substation_manager.util;

import lombok.experimental.UtilityClass;
import org.springframework.web.servlet.ModelAndView;

@UtilityClass
public class LoginPageUtil {

    public void addMessages(ModelAndView modelAndView, String errorMessage, String registered) {
        if (errorMessage != null) {
            modelAndView.addObject("errorMessage", errorMessage);
        }

        if (registered != null) {
            modelAndView.addObject("registeredMessage", "Registration successful. You can now log in.");
        }
    }

}
