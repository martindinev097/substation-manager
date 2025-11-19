package com.buildingenergy.substation_manager.user.service;

import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.user.model.UserRole;
import com.buildingenergy.substation_manager.web.dto.RegisterRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class UserInit implements ApplicationRunner {

    private final UserService userService;

    public UserInit(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<User> users = userService.getAll();

        boolean isDefaultAdminPresent = users.stream().anyMatch(user -> user.getUsername().equals("admin"));

        if (!isDefaultAdminPresent) {
            createDefaultUser();
        }
    }

    private void createDefaultUser() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("admin")
                .email("admin@substation.bg")
                .password("admin")
                .confirmedPassword("admin")
                .build();

        User admin = userService.register(registerRequest);
        admin.setRole(UserRole.ADMIN);

        userService.save(admin);

        log.info("==============================================");
        log.info("Default admin credentials:");
        log.info("Username: admin");
        log.info("Password: admin");
        log.info("==============================================");
    }
}
