package com.buildingenergy.substation_manager.formula.dto;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyFormulaResponse {

    private BigDecimal pricePerKwh;
    private BigDecimal multiplier;
    private BigDecimal divider;

}
