package com.buildingenergy.substation_manager.meter.model;

import com.buildingenergy.substation_manager.user.model.User;
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

    @ManyToOne(optional = false)
    private Meter meter;

    @ManyToOne(optional = false)
    private User user;

    @Column(nullable = false)
    private String meterName;

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
