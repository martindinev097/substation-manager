package com.buildingenergy.substation_manager.aspect;

import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.user.model.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @AfterReturning(
            pointcut = "execution(* com.buildingenergy.substation_manager.user.service.UserService.register(..))",
            returning = "result"
    )
    public void afterUserRegistration(Object result) {
        User user = (User) result;

        log.info("User %s has registered successfully".formatted(user.getUsername()));
    }

    @AfterThrowing(
            pointcut = "execution(* com.buildingenergy.substation_manager.user.service.UserService.loadUserByUsername(..))",
            throwing = "ex"
    )
    public void userInactiveLogin(JoinPoint jp, Exception ex) {
        if (ex instanceof DisabledException) {
            String username = (String) jp.getArgs()[0];
            log.warn("Authentication rejected for inactive user: %s".formatted(username));
        }
    }

    @After("execution(* com.buildingenergy.substation_manager.user.service.UserService.updateProfile(..))")
    public void afterProfileUpdate(JoinPoint jp) {
        User user = (User) jp.getArgs()[0];

        log.info("User with id: [%s] updated his profile.".formatted(user.getId()));
    }

    @After("execution(* com.buildingenergy.substation_manager.user.service.UserService.changeStatus(..))")
    public void afterStatusChange(JoinPoint jp) {
        User targetUser = (User) jp.getArgs()[0];
        User adminUser  = (User) jp.getArgs()[1];

        log.info("Admin [%s] changed account status of user [%s] from [%s] to [%s].".formatted(
                adminUser.getUsername(),
                targetUser.getUsername(),
                targetUser.isActive() ? "Inactive" : "Active",
                targetUser.isActive() ? "Active" : "Inactive"
        ));
    }

    @AfterReturning(
            pointcut = "execution(* com.buildingenergy.substation_manager.user.service.UserService.updateRole(..))",
            returning = "updatedUser"
    )
    public void afterRoleChange(JoinPoint jp, User updatedUser) {
        User actingUser = (User) jp.getArgs()[1];

        String oldRole = updatedUser.getRole() == UserRole.ADMIN
                ? UserRole.USER.getDisplayName()
                : UserRole.ADMIN.getDisplayName();

        String newRole = updatedUser.getRole().getDisplayName();

        log.info("Admin [%s] changed role of user [%s] from [%s] to [%s]".formatted(
                actingUser.getUsername(),
                updatedUser.getUsername(),
                oldRole,
                newRole
        ));
    }


}
