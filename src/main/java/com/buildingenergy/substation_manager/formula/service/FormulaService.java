package com.buildingenergy.substation_manager.formula.service;

import com.buildingenergy.substation_manager.formula.client.FormulaClient;
import com.buildingenergy.substation_manager.formula.dto.CompanyFormulaRequest;
import com.buildingenergy.substation_manager.formula.dto.CompanyFormulaResponse;
import com.buildingenergy.substation_manager.formula.dto.MeterFormulaRequest;
import com.buildingenergy.substation_manager.formula.dto.MeterFormulaResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class FormulaService {

    private final FormulaClient client;

    public FormulaService(FormulaClient client) {
        this.client = client;
    }

    @Cacheable(value = "companyFormula", key = "#userId")
    public CompanyFormulaResponse getCompanyFormula(UUID userId) {
        try {
            return client.getCompanyFormula(userId).getBody();
        } catch (Exception e) {
            log.error("[S2S Call]: Failed to fetch formula for user [%s] because: [%s]".formatted(userId, e.getMessage()));
            return null;
        }
    }

    @CacheEvict(value = "companyFormula", key = "#userId")
    public boolean updateCompanyFormula(UUID userId, @Valid CompanyFormulaRequest request) {
        try {
            client.updateCompanyFormula(userId, request).getBody();
            return true;
        } catch (Exception e) {
            log.error("[S2S Call]: Failed to update formula for user [%s] because: [%s]".formatted(userId, e.getMessage()));
            return false;
        }
    }

    @Cacheable(value = "meterFormula", key = "#userId")
    public MeterFormulaResponse getMeterFormula(UUID userId) {
        try {
            return client.getMeterFormula(userId).getBody();
        } catch (Exception e) {
            log.error("[S2S Call]: Failed to fetch formula for user [%s] because: [%s]".formatted(userId, e.getMessage()));
            return null;
        }
    }

    @CacheEvict(value = "meterFormula", key = "#userId")
    public boolean updateMeterFormula(UUID userId, @Valid MeterFormulaRequest request) {
        try {
            client.updateMeterFormula(userId, request).getBody();
            return true;
        } catch (Exception e) {
            log.error("[S2S Call]: Failed to update formula for user [%s] because: [%s]".formatted(userId, e.getMessage()));
            return false;
        }
    }

}
