package com.buildingenergy.substation_manager.formula.dto;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeterFormulaResponse {

    private BigDecimal pricePerKwh;
    private BigDecimal divider;

}
