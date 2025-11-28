package com.buildingenergy.substation_manager.login.service;

import com.buildingenergy.substation_manager.login.model.LoginHistory;
import com.buildingenergy.substation_manager.login.model.LoginStatus;
import com.buildingenergy.substation_manager.login.repository.LoginHistoryRepository;
import com.buildingenergy.substation_manager.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class LoginHistoryService {

    private final LoginHistoryRepository loginHistoryRepository;

    public LoginHistoryService(LoginHistoryRepository loginHistoryRepository) {
        this.loginHistoryRepository = loginHistoryRepository;
    }

    public void recordLogin(String username, String ipAddress, LoginStatus status) {
        LoginHistory login = LoginHistory.builder()
                .username(username)
                .ipAddress(ipAddress)
                .loginTime(LocalDateTime.now())
                .loginStatus(status)
                .build();

        loginHistoryRepository.save(login);
    }

    public List<LoginHistory> getLoginHistoryForUser(User user) {
        return loginHistoryRepository.findTop5ByUsernameOrderByLoginTimeDesc(user.getUsername());
    }

    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24)
    private void cleanupOldLoginAttempts() {
        LocalDateTime olderThan = LocalDateTime.now().minusDays(30);

        long deletedCount = loginHistoryRepository.deleteByLoginTimeBefore(olderThan);

        if (deletedCount > 0) {
            log.info("Deleted %d login history records older than 30 days.".formatted(deletedCount));
        }
    }
}
