package com.buildingenergy.substation_manager.web.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReadingRequest {

    private UUID companyId;

    private BigDecimal oldReadingM1;

    private BigDecimal newReadingM1;

    private BigDecimal differenceM1;

    private BigDecimal oldReadingM2;

    private BigDecimal newReadingM2;

    private BigDecimal differenceM2;

    private BigDecimal totalConsumption;

    private BigDecimal totalCost;

}
