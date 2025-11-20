package com.buildingenergy.substation_manager.security;

import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.user.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class AccountStatusFilter extends OncePerRequestFilter {

    private final UserService userService;

    public AccountStatusFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserData userData) {
            UUID userId = userData.getUserId();
            User user = userService.getById(userId);

            if (!user.isActive()) {
                request.getSession().invalidate();
                SecurityContextHolder.clearContext();

                response.sendRedirect("/login?account-inactive");

                return;
            }
        }

        filterChain.doFilter(request, response);
    }

}
