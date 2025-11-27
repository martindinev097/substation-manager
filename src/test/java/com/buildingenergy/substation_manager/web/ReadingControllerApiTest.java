package com.buildingenergy.substation_manager.web;

import com.buildingenergy.substation_manager.company.service.CompanyService;
import com.buildingenergy.substation_manager.floor.model.Floor;
import com.buildingenergy.substation_manager.floor.service.FloorService;
import com.buildingenergy.substation_manager.login.handler.LoginFailureHandler;
import com.buildingenergy.substation_manager.login.handler.LoginSuccessHandler;
import com.buildingenergy.substation_manager.reading.service.ReadingHistoryService;
import com.buildingenergy.substation_manager.reading.service.ReadingService;
import com.buildingenergy.substation_manager.security.UserData;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.user.model.UserRole;
import com.buildingenergy.substation_manager.user.service.UserService;
import com.buildingenergy.substation_manager.web.controller.ReadingController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReadingController.class)
public class ReadingControllerApiTest {

    @MockitoBean
    private ReadingService readingService;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private FloorService floorService;
    @MockitoBean
    private CompanyService companyService;
    @MockitoBean
    private ReadingHistoryService readingHistoryService;
    @MockitoBean
    private LoginSuccessHandler loginSuccessHandler;
    @MockitoBean
    private LoginFailureHandler loginFailureHandler;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void postSaveAllReadings_shouldDoRedirect3xxToFloorPageOfTheAppropriateFloorNumberAndInvokeBackUpCurrentReadingsAfterUpdateAllReadings() throws Exception {
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setActive(true);

        when(userService.getById(userId)).thenReturn(user);
        when(companyService.findAllByFloorAndUser(2, user)).thenReturn(List.of());

        MockHttpServletRequestBuilder httpRequest = post("/readings/save")
                .param("floorNumber", "2")
                .param("wrapper", "")
                .with(user(authentication(userId)))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/floor/" + 2));

        verify(readingService).updateAllReadings(any(), any());
        verify(readingHistoryService).backupCurrentReadings(anyList());
    }

    @Test
    void postSwapReadings_shouldDoRedirect3xxToFloorPageWithAppropriateFloorNumberAndInvokeSwapAllReadingsForFloor() throws Exception {
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setActive(true);

        when(userService.getById(userId)).thenReturn(user);
        when(floorService.findByFloorNumberAndUser(2, user)).thenReturn(new Floor());

        MockHttpServletRequestBuilder httpRequest = post("/readings/swap")
                .param("floorNumber", "2")
                .with(user(authentication(userId)))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/floor/" + 2));

        verify(readingService).swapAllReadingsForFloor(any(), any());
    }

    private UserData authentication(UUID userId) {
        return new UserData(userId, "mdinev", "123123", UserRole.USER, true);
    }

}
