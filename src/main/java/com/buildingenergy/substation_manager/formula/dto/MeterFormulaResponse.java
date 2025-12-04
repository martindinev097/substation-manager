package com.buildingenergy.substation_manager.formula.dto;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeterFormulaResponse implements Serializable {

    private BigDecimal pricePerKwh;
    private BigDecimal divider;

}
