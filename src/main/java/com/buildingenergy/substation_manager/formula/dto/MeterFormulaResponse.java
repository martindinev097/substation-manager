package com.buildingenergy.substation_manager.formula.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
public class MeterFormulaResponse {

    private BigDecimal pricePerKwh;
    private BigDecimal divider;

}
