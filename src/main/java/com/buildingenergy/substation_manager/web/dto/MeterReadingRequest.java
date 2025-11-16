package com.buildingenergy.substation_manager.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MeterReadingRequest {

    @NotNull
    private String meterName;

    @NotNull
    private String outsideBody;

    private String room;

    private String description;

    private BigDecimal energyPercentage;

    private BigDecimal oldReadings;

    private BigDecimal newReadings;

    private BigDecimal differenceReadings;

    private BigDecimal totalCost;

    @NotNull
    private LocalDateTime createdOn;

}
