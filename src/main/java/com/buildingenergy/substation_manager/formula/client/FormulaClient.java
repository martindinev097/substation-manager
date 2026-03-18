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

@FeignClient(name = "energy-formula-svc", url = "${microservice.url}", configuration = FeignConfiguration.class)
public interface FormulaClient {

    @GetMapping("/api/v1/company/formula")
    ResponseEntity<CompanyFormulaResponse> getCompanyFormula(@RequestParam("userId") UUID userId);

    @PutMapping("/api/v1/company/formula")
    ResponseEntity<CompanyFormulaResponse> updateCompanyFormula(@RequestParam("userId") UUID userId, @RequestBody CompanyFormulaRequest request);

    @GetMapping("/api/v1/meter/formula")
    ResponseEntity<MeterFormulaResponse> getMeterFormula(@RequestParam("userId") UUID userId);

    @PutMapping("/api/v1/meter/formula")
    ResponseEntity<MeterFormulaResponse> updateMeterFormula(@RequestParam("userId") UUID userId, @RequestBody MeterFormulaRequest request);
}
