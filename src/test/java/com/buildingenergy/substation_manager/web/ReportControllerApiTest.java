package com.buildingenergy.substation_manager.web;

import com.buildingenergy.substation_manager.exception.CannotExportEmptyCompanyHistory;
import com.buildingenergy.substation_manager.exception.CannotExportEmptyMetersHistory;
import com.buildingenergy.substation_manager.login.handler.LoginFailureHandler;
import com.buildingenergy.substation_manager.login.handler.LoginSuccessHandler;
import com.buildingenergy.substation_manager.meter.model.MeterHistory;
import com.buildingenergy.substation_manager.meter.service.MeterHistoryService;
import com.buildingenergy.substation_manager.reading.model.ReadingHistory;
import com.buildingenergy.substation_manager.reading.service.ReadingHistoryService;
import com.buildingenergy.substation_manager.report.service.ExcelExportService;
import com.buildingenergy.substation_manager.security.UserData;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.user.model.UserRole;
import com.buildingenergy.substation_manager.user.service.UserService;
import com.buildingenergy.substation_manager.web.controller.ReportController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportController.class)
public class ReportControllerApiTest {

    @MockitoBean
    private ReadingHistoryService readingHistoryService;
    @MockitoBean
    private MeterHistoryService meterHistoryService;
    @MockitoBean
    private ExcelExportService excelExportService;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private LoginSuccessHandler loginSuccessHandler;
    @MockitoBean
    private LoginFailureHandler loginFailureHandler;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getReportsPage_shouldReturnStatus200OkAndShowReportsViewWithAllNecessaryAttributes() throws Exception {
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setActive(true);
        user.setRole(UserRole.USER);

        when(userService.getById(userId)).thenReturn(user);
        when(readingHistoryService.getAllByMonthAndUser(2, user)).thenReturn(List.of());
        when(meterHistoryService.getAllByMonthAndUser(2, user)).thenReturn(List.of());

        UserData authentication = authentication(userId);

        MockHttpServletRequestBuilder httpRequest = get("/reports")
                .param("month", "2")
                .with(user(authentication));

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("reports"))
                .andExpect(model().attributeExists(
                        "currentPage",
                        "readingHistory",
                        "selectedMonth",
                        "meterHistory"
                ));
    }

    @Test
    void getExportToExcelWithNonEmptyReadingHistoryList_shouldReturn200OkAndInvokeExportReadingHistoryMethod() throws Exception {
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setActive(true);
        user.setRole(UserRole.USER);

        ReadingHistory r = ReadingHistory.builder()
                .oldReadingM1(BigDecimal.ONE)
                .newReadingM1(BigDecimal.ONE)
                .oldReadingM2(BigDecimal.ONE)
                .newReadingM2(BigDecimal.TEN)
                .build();

        when(userService.getById(userId)).thenReturn(user);
        when(readingHistoryService.getAllByMonthAndUser(2, user)).thenReturn(List.of(r));

        MockHttpServletRequestBuilder httpRequest = get("/reports/company/export")
                .param("month", "2")
                .with(user(authentication(userId)));

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk());

        verify(excelExportService).exportReadingHistory(anyList(), any(), anyInt());
    }

    @Test
    void getExportToExcelWithEmptyReadingHistoryList_shouldThrowCannotExportEmptyCompanyHistoryAndRedirectToReportsPageOfTheMonth() throws Exception {
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setActive(true);
        user.setRole(UserRole.USER);

        when(userService.getById(userId)).thenReturn(user);
        when(readingHistoryService.getAllByMonthAndUser(2, user)).thenReturn(List.of());
        doThrow(new CannotExportEmptyCompanyHistory("No companies found", 2)).when(excelExportService).exportReadingHistory(anyList(), any(), anyInt());

        MockHttpServletRequestBuilder httpRequest = get("/reports/company/export")
                .param("month", "2")
                .with(user(authentication(userId)));

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reports?month=" + 2))
                .andExpect(flash().attributeExists("emptyMonthMessage"));
    }

    @Test
    void getExportMetersToExcelWithNonEmptyMeterHistoryList_shouldReturn200OkAndInvokeExportMeterHistoryMethod() throws Exception {
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setActive(true);
        user.setRole(UserRole.USER);

        MeterHistory r = MeterHistory.builder()
                .oldReadings(BigDecimal.ONE)
                .newReadings(BigDecimal.ONE)
                .build();

        when(userService.getById(userId)).thenReturn(user);
        when(meterHistoryService.getAllByMonthAndUser(2, user)).thenReturn(List.of(r));

        MockHttpServletRequestBuilder httpRequest = get("/reports/meters/export")
                .param("month", "2")
                .with(user(authentication(userId)));

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk());

        verify(excelExportService).exportMeterHistory(anyList(), any(), anyInt());
    }

    @Test
    void getExportMetersToExcelWithEmptyMeterHistoryList_shouldThrowCannotExportEmptyMeterHistoryAndRedirectToReportsPageOfTheRelevantMonth() throws Exception {
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setActive(true);
        user.setRole(UserRole.USER);

        when(userService.getById(userId)).thenReturn(user);
        when(meterHistoryService.getAllByMonthAndUser(2, user)).thenReturn(List.of());
        doThrow(new CannotExportEmptyMetersHistory("No meters found", 2)).when(excelExportService).exportMeterHistory(anyList(), any(), anyInt());

        MockHttpServletRequestBuilder httpRequest = get("/reports/meters/export")
                .param("month", "2")
                .with(user(authentication(userId)));

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reports?month=" + 2))
                .andExpect(flash().attributeExists("emptyMeterHistory"));
    }

    @Test
    void deleteCompany_shouldReturnRedirect3xxWithDeletedMessageRedirectAttributeAndInvokeDeleteCompanyByIdAndMonth() throws Exception {
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setActive(true);
        user.setRole(UserRole.USER);

        when(userService.getById(userId)).thenReturn(user);

        MockHttpServletRequestBuilder httpRequest = delete("/reports/company/delete/" + UUID.randomUUID() + "/" + 2)
                .with(user(authentication(userId)))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reports?month=" + 2))
                .andExpect(flash().attributeExists("deletedMessage"));

        verify(readingHistoryService).deleteCompanyByIdAndMonth(any(), anyInt());
    }

    @Test
    void deleteMeter_shouldReturnRedirect3xxWithDeletedMeterMessageRedirectAttributeAndInvokeDeleteMeterByIdAndMonth() throws Exception {
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setActive(true);
        user.setRole(UserRole.USER);

        when(userService.getById(userId)).thenReturn(user);

        MockHttpServletRequestBuilder httpRequest = delete("/reports/meter/delete/" + UUID.randomUUID() + "/" + 2)
                .with(user(authentication(userId)))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reports?month=" + 2))
                .andExpect(flash().attributeExists("deletedMeterMessage"));

        verify(meterHistoryService).deleteMeterByIdAndMonth(any(), anyInt());
    }

    private UserData authentication(UUID userId) {
        return new UserData(userId, "mdinev", "123123", UserRole.USER, true);
    }

}
