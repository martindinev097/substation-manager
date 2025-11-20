package com.buildingenergy.substation_manager.meter.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "meter_history")
public class MeterHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID meterIdSnapshot;

    private UUID userIdSnapshot;

    private String meterNameSnapshot;

    @Column(nullable = false)
    private String outsideBody;

    @Column(nullable = false)
    private String room;

    private String description;

    @Column(nullable = false)
    private BigDecimal energyPercentage;

    @Column(nullable = false)
    private BigDecimal oldReadings;

    @Column(nullable = false)
    private BigDecimal newReadings;

    private BigDecimal differenceReadings;

    private BigDecimal totalCost;

    @Column(nullable = false)
    private LocalDateTime savedAt;
}
