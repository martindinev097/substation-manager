package com.buildingenergy.substation_manager.web;

import com.buildingenergy.substation_manager.security.UserData;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.user.model.UserRole;
import com.buildingenergy.substation_manager.user.service.UserService;
import com.buildingenergy.substation_manager.web.controller.AdminController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
public class AdminControllerApiTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAdminPanel_shouldReturn200OkWithUserAndAdminsAndCurrentPageAttributes() throws Exception {
        UserData authentication = authentication();

        User user = new User();
        user.setActive(true);

        when(userService.getById(any())).thenReturn(user);
        when(userService.findAll()).thenReturn(List.of());
        when(userService.findAllAdmins()).thenReturn(List.of());

        MockHttpServletRequestBuilder httpRequest = get("/admin-panel")
                .with(user(authentication));

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("admin-panel"))
                .andExpect(model().attributeExists("currentPage", "users", "admins"));
    }

    @Test
    void patchChangeRole_shouldInvokeUpdateRoleAndReturn3xxRedirectToAdminPanelPage() throws Exception {
        UserData authentication = authentication();

        User user = new User();
        user.setActive(true);

        when(userService.getById(any())).thenReturn(user);

        MockHttpServletRequestBuilder httpRequest = patch("/admin-panel/role/change/" + UUID.randomUUID())
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin-panel"));

        verify(userService).updateRole(any(), any());
    }

    @Test
    void patchDeactivateUser_shouldInvokeChangeStatusAndRedirectToAdminPanelPage() throws Exception {
        UserData authentication = authentication();

        User user = new User();
        user.setActive(true);

        User admin = new User();
        admin.setActive(true);
        admin.setRole(UserRole.ADMIN);

        when(userService.getById(any())).thenReturn(user);
        when(userService.getById(any())).thenReturn(admin);

        MockHttpServletRequestBuilder httpRequest = patch("/admin-panel/deactivate/" + UUID.randomUUID())
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin-panel"));

        verify(userService).changeStatus(any(), any());
    }

    private UserData authentication() {
        return UserData.builder()
                .userId(UUID.randomUUID())
                .isActive(true)
                .role(UserRole.ADMIN)
                .username("mdinev")
                .password("123123")
                .build();
    }

}
