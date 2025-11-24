package com.buildingenergy.substation_manager.formula.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MeterFormulaRequest {

    @NotNull
    @Positive
    private BigDecimal pricePerKwh;

    @NotNull
    @Positive
    private BigDecimal divider;

}
