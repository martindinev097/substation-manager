package com.buildingenergy.substation_manager.user.model;

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
@Entity(name = "readings")
public class Reading {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Company company;

    private BigDecimal oldReadingM1;

    private BigDecimal newReadingM1;

    private BigDecimal differenceM1;

    private BigDecimal oldReadingM2;

    private BigDecimal newReadingM2;

    private BigDecimal differenceM2;

    private BigDecimal totalConsumption;

    private BigDecimal totalCost;

    private LocalDateTime createdOn;

}
