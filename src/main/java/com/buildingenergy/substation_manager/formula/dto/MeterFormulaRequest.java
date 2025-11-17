package com.buildingenergy.substation_manager.formula.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MeterFormulaRequest {

    private BigDecimal pricePerKwh;
    private BigDecimal divider;

}
