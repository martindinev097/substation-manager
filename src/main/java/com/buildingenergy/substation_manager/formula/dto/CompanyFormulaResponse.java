package com.buildingenergy.substation_manager.formula.dto;

import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;

@Data
@Getter
public class CompanyFormulaResponse {

    private BigDecimal pricePerKwh;
    private BigDecimal multiplier;
    private BigDecimal divider;

}
