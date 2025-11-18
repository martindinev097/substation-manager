package com.buildingenergy.substation_manager.formula.service;

import com.buildingenergy.substation_manager.formula.client.FormulaClient;
import com.buildingenergy.substation_manager.formula.dto.CompanyFormulaRequest;
import com.buildingenergy.substation_manager.formula.dto.CompanyFormulaResponse;
import com.buildingenergy.substation_manager.formula.dto.MeterFormulaRequest;
import com.buildingenergy.substation_manager.formula.dto.MeterFormulaResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class FormulaService {

    private final FormulaClient client;

    public FormulaService(FormulaClient client) {
        this.client = client;
    }

    public CompanyFormulaResponse getCompanyFormula(UUID userId) {
        try {
            return client.getCompanyFormula(userId).getBody();
        } catch (Exception e) {
            log.error("[S2S Call]: Failed to fetch formula for user [%s] because: [%s]".formatted(userId, e.getMessage()));
            return null;
        }
    }

    public boolean updateCompanyFormula(UUID userId, CompanyFormulaRequest request) {
        try {
            client.updateCompanyFormula(userId, request).getBody();
            return true;
        } catch (Exception e) {
            log.error("[S2S Call]: Failed to update formula for user [%s] because: [%s]".formatted(userId, e.getMessage()));
            return false;
        }
    }

    public MeterFormulaResponse getMeterFormula(UUID userId) {
        try {
            return client.getMeterFormula(userId).getBody();
        } catch (Exception e) {
            log.error("[S2S Call]: Failed to fetch formula for user [%s] because: [%s]".formatted(userId, e.getMessage()));
            return null;
        }
    }

    public boolean updateMeterFormula(UUID userId, MeterFormulaRequest request) {
        try {
            client.updateMeterFormula(userId, request).getBody();
            return true;
        } catch (Exception e) {
            log.error("[S2S Call]: Failed to update formula for user [%s] because: [%s]".formatted(userId, e.getMessage()));
            return false;
        }
    }

}
