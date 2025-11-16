package com.buildingenergy.substation_manager.formula.service;

import com.buildingenergy.substation_manager.config.FormulaConfiguration;
import com.buildingenergy.substation_manager.formula.client.CompanyFormulaClient;
import com.buildingenergy.substation_manager.formula.dto.CompanyFormulaRequest;
import com.buildingenergy.substation_manager.formula.dto.CompanyFormulaResponse;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
public class CompanyFormulaService {

    private final CompanyFormulaClient client;
    private final FormulaConfiguration config;

    public CompanyFormulaService(CompanyFormulaClient client, FormulaConfiguration config) {
        this.client = client;
        this.config = config;
    }

    public CompanyFormulaResponse getFormula(UUID userId) {
        try {
            return client.getFormula(userId).getBody();
        } catch (FeignException e) {
            log.error("[S2S Call]: Failed to fetch formula for user [%s] because: [%s]".formatted(userId, e.getMessage()));
//            return CompanyFormulaResponse.builder()
//                    .pricePerKwh(BigDecimal.ZERO)
//                    .multiplier(BigDecimal.ZERO)
//                    .divider(BigDecimal.ZERO)
//                    .build();
            return null;
        } catch (Exception e) {
            log.error("Unexpected error while fetching formula for user [%s] because: [%s]".formatted(userId, e.getMessage()));
//            return CompanyFormulaResponse.builder()
//                    .pricePerKwh(BigDecimal.ZERO)
//                    .multiplier(BigDecimal.ZERO)
//                    .divider(BigDecimal.ZERO)
//                    .build();
            return null;
        }
    }

    public boolean updateFormula(UUID userId, CompanyFormulaRequest request) {
        try {
            client.updateFormula(config.getKey(), userId, request).getBody();
            return true;
        } catch (FeignException e) {
            log.error("[S2S Call]: Failed to update formula for user [%s] because: [%s]".formatted(userId, e.getMessage()));
            return false;
        } catch (Exception e) {
            log.error("Unexpected error while updating formula for user [%s] because: [%s]".formatted(userId, e.getMessage()));
            return false;
        }
    }

}
