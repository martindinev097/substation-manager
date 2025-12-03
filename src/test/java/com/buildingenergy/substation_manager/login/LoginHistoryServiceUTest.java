package com.buildingenergy.substation_manager.login;

import com.buildingenergy.substation_manager.login.model.LoginHistory;
import com.buildingenergy.substation_manager.login.model.LoginStatus;
import com.buildingenergy.substation_manager.login.repository.LoginHistoryRepository;
import com.buildingenergy.substation_manager.login.service.LoginHistoryService;
import com.buildingenergy.substation_manager.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginHistoryServiceUTest {

    @Mock
    private LoginHistoryRepository loginHistoryRepository;

    @InjectMocks
    private LoginHistoryService loginHistoryService;

    @Test
    void givenCorrectData_whenRecordLogin_thenSaveHistory() {
        ArgumentCaptor<LoginHistory> captor = ArgumentCaptor.forClass(LoginHistory.class);

        loginHistoryService.recordLogin("mdinev", "127.0.0.1", LoginStatus.SUCCEEDED);

        verify(loginHistoryRepository, times(1)).save(captor.capture());

        LoginHistory saved = captor.getValue();

        assertEquals("mdinev", saved.getUsername());
        assertEquals("127.0.0.1", saved.getIpAddress());
        assertEquals(LoginStatus.SUCCEEDED, saved.getLoginStatus());
        assertNotNull(saved.getLoginTime());
    }

    @Test
    void givenUser_whenGetLoginHistory_thenReturnList() {
        User user = new User();
        user.setUsername("mdinev");

        LoginHistory l1 = LoginHistory.builder().username("mdinev").build();
        LoginHistory l2 = LoginHistory.builder().username("mdinev").build();

        when(loginHistoryRepository.findTop5ByUsernameOrderByLoginTimeDesc("mdinev"))
                .thenReturn(List.of(l1, l2));

        List<LoginHistory> result = loginHistoryService.getLoginHistoryForUser(user);

        assertEquals(2, result.size());
        assertEquals("mdinev", result.get(0).getUsername());
        assertEquals("mdinev", result.get(1).getUsername());
    }

}
