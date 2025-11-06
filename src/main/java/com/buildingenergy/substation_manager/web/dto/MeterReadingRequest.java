package com.buildingenergy.substation_manager.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MeterReadingRequest {

    private String meterName;

    private String outsideBody;

    private String room;

    private String description;

    private BigDecimal energyPercentage;

    private BigDecimal oldReadings;

    private BigDecimal newReadings;

    private BigDecimal differenceReadings;

    private BigDecimal totalCost;

}
