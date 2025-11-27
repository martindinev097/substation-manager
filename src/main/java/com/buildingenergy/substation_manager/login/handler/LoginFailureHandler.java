package com.buildingenergy.substation_manager.login.handler;

import com.buildingenergy.substation_manager.exception.UsernameDoesNotExist;
import com.buildingenergy.substation_manager.login.model.LoginStatus;
import com.buildingenergy.substation_manager.login.service.LoginHistoryService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final LoginHistoryService loginHistoryService;

    public LoginFailureHandler(LoginHistoryService loginHistoryService) {
        this.loginHistoryService = loginHistoryService;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String username = request.getParameter("username");
        String ipAddress = request.getRemoteAddr();

        loginHistoryService.recordLogin(username, ipAddress, LoginStatus.FAILED);

        Throwable cause = exception.getCause();

        if (cause instanceof DisabledException || exception instanceof DisabledException) {
            response.sendRedirect("/login?account-inactive");
        } else if (cause instanceof UsernameDoesNotExist) {
            response.sendRedirect("/login?username-not-exist");
        } else {
            response.sendRedirect("/login?error");
        }
    }
}
