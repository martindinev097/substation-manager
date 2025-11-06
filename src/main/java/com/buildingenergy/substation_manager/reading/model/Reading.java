package com.buildingenergy.substation_manager.reading.model;

import com.buildingenergy.substation_manager.company.model.Company;
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

    @Column(name = "old_reading_m1")
    private BigDecimal oldReadingM1;

    @Column(name = "new_reading_m1")
    private BigDecimal newReadingM1;

    @Column(name = "difference_m1")
    private BigDecimal differenceM1;

    @Column(name = "old_reading_m2")
    private BigDecimal oldReadingM2;

    @Column(name = "new_reading_m2")
    private BigDecimal newReadingM2;

    @Column(name = "difference_m2")
    private BigDecimal differenceM2;

    private BigDecimal totalConsumption;

    private BigDecimal totalCost;

    private LocalDateTime createdOn;

}
