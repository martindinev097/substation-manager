package com.buildingenergy.substation_manager.formula.client;

import com.buildingenergy.substation_manager.formula.dto.CompanyFormulaRequest;
import com.buildingenergy.substation_manager.formula.dto.CompanyFormulaResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "energy-formula-svc", url = "http://localhost:8081/api/v1/formula")
public interface CompanyFormulaClient {

    @GetMapping
    ResponseEntity<CompanyFormulaResponse> getFormula(@RequestParam("userId") UUID userId);

    @GetMapping("/cost")
    ResponseEntity<BigDecimal> calculateCost(@RequestParam("userId") UUID userId, @RequestParam BigDecimal differenceReadings);

    @PostMapping
    ResponseEntity<CompanyFormulaResponse> updateFormula(@RequestHeader("SM-API-KEY") String apiKey, @RequestParam("userId") UUID userId, @RequestBody CompanyFormulaRequest request);
}
