package com.buildingenergy.substation_manager.formula.client;

import com.buildingenergy.substation_manager.config.FeignConfiguration;
import com.buildingenergy.substation_manager.formula.dto.CompanyFormulaRequest;
import com.buildingenergy.substation_manager.formula.dto.CompanyFormulaResponse;
import com.buildingenergy.substation_manager.formula.dto.MeterFormulaRequest;
import com.buildingenergy.substation_manager.formula.dto.MeterFormulaResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "energy-formula-svc", url = "http://localhost:8081/api/v1", configuration = FeignConfiguration.class)
public interface FormulaClient {

    @GetMapping("/company/formula")
    ResponseEntity<CompanyFormulaResponse> getCompanyFormula(@RequestParam("userId") UUID userId);

    @PutMapping("/company/formula")
    ResponseEntity<CompanyFormulaResponse> updateCompanyFormula(@RequestParam("userId") UUID userId, @RequestBody CompanyFormulaRequest request);

    @GetMapping("/meter/formula")
    ResponseEntity<MeterFormulaResponse> getMeterFormula(@RequestParam("userId") UUID userId);

    @PutMapping("/meter/formula")
    ResponseEntity<MeterFormulaResponse> updateMeterFormula(@RequestParam("userId") UUID userId, @RequestBody MeterFormulaRequest request);
}
