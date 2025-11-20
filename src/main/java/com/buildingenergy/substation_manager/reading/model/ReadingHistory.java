package com.buildingenergy.substation_manager.reading.model;

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
@Entity(name = "reading_history")
public class ReadingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID companyIdSnapshot;
    private UUID userIdSnapshot;
    private String companyNameSnapshot;

    private String office;

    private BigDecimal oldReadingM1;
    private BigDecimal newReadingM1;
    private BigDecimal differenceM1;
    private BigDecimal oldReadingM2;
    private BigDecimal newReadingM2;
    private BigDecimal differenceM2;
    private BigDecimal totalConsumption;
    private BigDecimal totalCost;

    @Column(nullable = false)
    private LocalDateTime savedAt;
}
