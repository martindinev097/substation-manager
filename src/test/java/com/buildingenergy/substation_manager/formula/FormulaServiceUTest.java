package com.buildingenergy.substation_manager.formula;

import com.buildingenergy.substation_manager.formula.client.FormulaClient;
import com.buildingenergy.substation_manager.formula.dto.CompanyFormulaRequest;
import com.buildingenergy.substation_manager.formula.dto.CompanyFormulaResponse;
import com.buildingenergy.substation_manager.formula.dto.MeterFormulaRequest;
import com.buildingenergy.substation_manager.formula.dto.MeterFormulaResponse;
import com.buildingenergy.substation_manager.formula.service.FormulaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FormulaServiceUTest {

    @Mock
    private FormulaClient client;

    @InjectMocks
    private FormulaService formulaService;

    @Test
    void givenValidUserId_whenGetCompanyFormula_thenReturnResponse() {
        UUID userId = UUID.randomUUID();

        CompanyFormulaResponse response = new CompanyFormulaResponse();

        when(client.getCompanyFormula(userId)).thenReturn(ResponseEntity.ok(response));

        CompanyFormulaResponse result = formulaService.getCompanyFormula(userId);

        assertNotNull(result);
        assertEquals(response, result);

        verify(client).getCompanyFormula(userId);
    }

    @Test
    void givenClientThrowsException_whenGetCompanyFormula_thenReturnNull() {
        UUID userId = UUID.randomUUID();

        when(client.getCompanyFormula(userId)).thenThrow(new RuntimeException());

        CompanyFormulaResponse result = formulaService.getCompanyFormula(userId);

        assertNull(result);

        verify(client).getCompanyFormula(userId);
    }

    @Test
    void givenValidRequest_whenUpdateCompanyFormula_thenReturnTrue() {
        UUID userId = UUID.randomUUID();

        CompanyFormulaRequest request = new CompanyFormulaRequest();

        when(client.updateCompanyFormula(userId, request)).thenReturn(ResponseEntity.ok().build());

        boolean result = formulaService.updateCompanyFormula(userId, request);

        assertTrue(result);

        verify(client).updateCompanyFormula(userId, request);
    }

    @Test
    void givenClientThrowsException_whenUpdateCompanyFormula_thenReturnFalse() {
        UUID userId = UUID.randomUUID();

        CompanyFormulaRequest request = new CompanyFormulaRequest();

        when(client.updateCompanyFormula(userId, request)).thenThrow(new RuntimeException());

        boolean result = formulaService.updateCompanyFormula(userId, request);

        assertFalse(result);

        verify(client).updateCompanyFormula(userId, request);
    }

    @Test
    void givenValidUserId_whenGetMeterFormula_thenReturnResponse() {
        UUID userId = UUID.randomUUID();

        MeterFormulaResponse response = new MeterFormulaResponse();

        when(client.getMeterFormula(userId)).thenReturn(ResponseEntity.ok(response));

        MeterFormulaResponse result = formulaService.getMeterFormula(userId);

        assertNotNull(result);
        assertEquals(response, result);

        verify(client).getMeterFormula(userId);
    }

    @Test
    void givenClientThrowsException_whenGetMeterFormula_thenReturnNull() {
        UUID userId = UUID.randomUUID();

        when(client.getMeterFormula(userId)).thenThrow(new RuntimeException());

        MeterFormulaResponse result = formulaService.getMeterFormula(userId);

        assertNull(result);

        verify(client).getMeterFormula(userId);
    }

    @Test
    void givenValidRequest_whenUpdateMeterFormula_thenReturnTrue() {
        UUID userId = UUID.randomUUID();

        MeterFormulaRequest request = new MeterFormulaRequest();

        when(client.updateMeterFormula(userId, request)).thenReturn(ResponseEntity.ok().build());

        boolean result = formulaService.updateMeterFormula(userId, request);

        assertTrue(result);

        verify(client).updateMeterFormula(userId, request);
    }

    @Test
    void givenClientThrowsException_whenUpdateMeterFormula_thenReturnFalse() {
        UUID userId = UUID.randomUUID();

        MeterFormulaRequest request = new MeterFormulaRequest();

        when(client.updateMeterFormula(userId, request)).thenThrow(new RuntimeException("fail"));

        boolean result = formulaService.updateMeterFormula(userId, request);

        assertFalse(result);

        verify(client).updateMeterFormula(userId, request);
    }

}
