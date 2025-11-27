package com.buildingenergy.substation_manager.login.handler;

import com.buildingenergy.substation_manager.login.model.LoginStatus;
import com.buildingenergy.substation_manager.login.service.LoginHistoryService;
import com.buildingenergy.substation_manager.security.UserData;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final LoginHistoryService loginHistoryService;

    public LoginSuccessHandler(LoginHistoryService loginHistoryService) {
        this.loginHistoryService = loginHistoryService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserData userData = (UserData) authentication.getPrincipal();

        String username = userData.getUsername();
        String ipAddress = request.getRemoteAddr();

        loginHistoryService.recordLogin(username, ipAddress, LoginStatus.SUCCEEDED);

        response.sendRedirect("/home");
    }
}
