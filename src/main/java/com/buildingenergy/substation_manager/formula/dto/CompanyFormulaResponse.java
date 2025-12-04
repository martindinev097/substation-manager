package com.buildingenergy.substation_manager.formula.dto;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyFormulaResponse implements Serializable {

    private BigDecimal pricePerKwh;
    private BigDecimal multiplier;
    private BigDecimal divider;

}
