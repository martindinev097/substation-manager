package com.buildingenergy.substation_manager.web;

import com.buildingenergy.substation_manager.exception.FloorNotFound;
import com.buildingenergy.substation_manager.floor.model.Floor;
import com.buildingenergy.substation_manager.floor.service.FloorService;
import com.buildingenergy.substation_manager.formula.dto.MeterFormulaResponse;
import com.buildingenergy.substation_manager.formula.service.FormulaService;
import com.buildingenergy.substation_manager.meter.model.Meter;
import com.buildingenergy.substation_manager.meter.service.MeterHistoryService;
import com.buildingenergy.substation_manager.meter.service.MeterService;
import com.buildingenergy.substation_manager.security.UserData;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.user.model.UserRole;
import com.buildingenergy.substation_manager.user.service.UserService;
import com.buildingenergy.substation_manager.web.controller.MeterController;
import com.buildingenergy.substation_manager.web.dto.MeterReadingWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MeterController.class)
public class MeterControllerApiTest {

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private FloorService floorService;
    @MockitoBean
    private MeterService meterService;
    @MockitoBean
    private MeterHistoryService meterHistoryService;
    @MockitoBean
    private FormulaService formulaService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getMetersPage_shouldReturn200OkWithAllNecessaryAttributes() throws Exception {
        UUID userId = UUID.randomUUID();

        UserData authentication = new UserData(userId, "mdinev", "123123", UserRole.USER, true);

        User user = new User();
        user.setId(userId);
        user.setActive(true);
        user.setRole(UserRole.USER);

        Floor floor = Floor.builder()
                .user(user)
                .floorNumber(1)
                .build();

        List<Meter> meters = List.of(new Meter());

        MeterFormulaResponse formula = new MeterFormulaResponse(BigDecimal.ONE, BigDecimal.ONE);

        MeterReadingWrapper wrapper = new MeterReadingWrapper();

        when(userService.getById(userId)).thenReturn(user);
        when(floorService.findByFloorNumberAndUser(floor.getFloorNumber(), user)).thenReturn(floor);
        when(meterService.findAllByFloorAndUser(floor, user)).thenReturn(meters);
        when(formulaService.getMeterFormula(userId)).thenReturn(formula);
        when(meterService.buildMeterReadingWrapper(meters)).thenReturn(wrapper);
        when(meterService.areSwapped(user, floor)).thenReturn(false);

        MockHttpServletRequestBuilder httpRequest = get("/meters/floor/" + 1).with(user(authentication));

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("meters"))
                .andExpect(model().attributeExists(
                        "floorNumber",
                        "currentPage",
                        "meters",
                        "meterFormula",
                        "meterRequest",
                        "readingWrapper",
                        "areSwapped"
                ));
    }

    @Test
    void postAddMeterWithValidDetails_shouldReturn200OkAndRedirectToMetersPageWithTheCurrentFloor() throws Exception {
        UUID userId = UUID.randomUUID();

        UserData authentication = new UserData(userId, "mdinev", "123123", UserRole.USER, true);

        User user = new User();
        user.setId(userId);
        user.setActive(true);
        user.setRole(UserRole.USER);

        Floor floor = Floor.builder()
                .user(user)
                .floorNumber(1)
                .build();

        List<Meter> meters = List.of(new Meter());

        when(userService.getById(userId)).thenReturn(user);
        when(floorService.findByFloorNumberAndUser(floor.getFloorNumber(), user)).thenReturn(floor);
        when(meterService.findAllByFloorAndUser(floor, user)).thenReturn(meters);

        MockHttpServletRequestBuilder httpRequest = post("/meters/floor/" + 1)
                .formField("meterName", "19")
                .formField("outsideBody", "2")
                .formField("room", "2")
                .formField("description", "test")
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/meters/floor/" + 1));

        verify(meterService, times(1)).createMeter(any(), any(), anyInt(), any());
    }

    @Test
    void postAddMeterWithBlankName_shouldReturnMetersPageAndNotRedirect() throws Exception {
        UUID userId = UUID.randomUUID();

        UserData authentication = new UserData(userId, "mdinev", "123123", UserRole.USER, true);

        User user = new User();
        user.setId(userId);
        user.setActive(true);
        user.setRole(UserRole.USER);

        Floor floor = Floor.builder()
                .user(user)
                .floorNumber(1)
                .build();

        List<Meter> meters = List.of(new Meter());

        when(userService.getById(userId)).thenReturn(user);
        when(floorService.findByFloorNumberAndUser(floor.getFloorNumber(), user)).thenReturn(floor);
        when(meterService.findAllByFloorAndUser(floor, user)).thenReturn(meters);
        when(formulaService.getMeterFormula(userId)).thenReturn(new MeterFormulaResponse());
        when(meterService.buildMeterReadingWrapper(meters)).thenReturn(new MeterReadingWrapper());

        MockHttpServletRequestBuilder httpRequest = post("/meters/floor/" + 1)
                .formField("meterName", "")
                .formField("outsideBody", "2")
                .formField("room", "2")
                .formField("description", "test")
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("meters"))
                .andExpect(model().attributeExists(
                        "errorMessage",
                        "floorNumber",
                        "currentPage",
                        "meters",
                        "readingWrapper",
                        "meterFormula"
                ));

        verify(meterService, never()).createMeter(any(), any(), anyInt(), any());
    }

    @Test
    void postSaveMeterReadingsOnAMeterFloorPage_shouldReturn3xxRedirectToTheSameFloorPage() throws Exception {
        UUID userId = UUID.randomUUID();

        UserData authentication = new UserData(userId, "mdinev", "123123", UserRole.USER, true);

        User user = new User();
        user.setId(userId);
        user.setActive(true);
        user.setRole(UserRole.USER);

        Floor floor = Floor.builder()
                .user(user)
                .floorNumber(1)
                .build();

        when(userService.getById(userId)).thenReturn(user);
        when(floorService.findByFloorNumberAndUser(floor.getFloorNumber(), user)).thenReturn(floor);
        when(meterService.findAllByFloorAndUser(floor, user)).thenReturn(List.of());

        MockHttpServletRequestBuilder httpRequest = post("/meters/save")
                .param("floorNumber", "1")
                .param("readings[0].meterName", "19")
                .param("readings[0].outsideBody", "2")
                .param("readings[0].createdOn", LocalDateTime.now().toString())
                .with(csrf())
                .with(user(authentication));

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/meters/floor/" + 1));

        verify(meterService).updateMeterReadings(any(), any(), any());
        verify(meterHistoryService).backupCurrentReadings(anyList(), any());
    }

    @Test
    void postSwapMeterReadings_shouldReturn3xxRedirectToMeterFloorNumberPage() throws Exception {
        UUID userId = UUID.randomUUID();

        UserData authentication = new UserData(userId, "mdinev", "123123", UserRole.USER, true);

        User user = new User();
        user.setId(userId);
        user.setActive(true);
        user.setRole(UserRole.USER);

        Floor floor = Floor.builder()
                .floorNumber(1)
                .build();

        when(userService.getById(userId)).thenReturn(user);
        when(floorService.findByFloorNumberAndUser(1, user)).thenReturn(floor);
        when(meterService.findAllByFloorAndUser(floor, user)).thenReturn(List.of());

        MockHttpServletRequestBuilder httpRequest = post("/meters/swap")
                .param("floorNumber", "1")
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/meters/floor/" + 1));

        verify(meterService).swapMeterReadings(any());
    }

    @Test
    void postSaveMeterReadingsOnAMeterFloorPageWithNoFloor_shouldReturnStatusNotFoundAndRedirectToNotFoundPage() throws Exception {
        UUID userId = UUID.randomUUID();

        UserData authentication = new UserData(userId, "mdinev", "123123", UserRole.USER, true);

        User user = new User();
        user.setId(userId);
        user.setActive(true);
        user.setRole(UserRole.USER);

        when(userService.getById(userId)).thenReturn(user);
        when(floorService.findByFloorNumberAndUser(2, user)).thenThrow(new FloorNotFound("Floor not found."));

        MockHttpServletRequestBuilder httpRequest = post("/meters/save")
                .param("floorNumber", "2")
                .param("readings[0].meterName", "19")
                .param("readings[0].outsideBody", "2")
                .param("readings[0].createdOn", LocalDateTime.now().toString())
                .with(csrf())
                .with(user(authentication));

        mockMvc.perform(httpRequest)
                .andExpect(status().isNotFound())
                .andExpect(view().name("not-found"));

        verify(meterService, never()).updateMeterReadings(any(), any(), any());
        verify(meterService, never()).findAllByFloorAndUser(any(), any());
        verify(meterHistoryService, never()).backupCurrentReadings(anyList(), any());
    }

}
