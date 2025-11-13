package com.buildingenergy.substation_manager.formula.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CompanyFormulaRequest {

    private BigDecimal pricePerKwh;
    private BigDecimal multiplier;
    private BigDecimal divider;

}
