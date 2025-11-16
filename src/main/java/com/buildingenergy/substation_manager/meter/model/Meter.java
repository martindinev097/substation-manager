package com.buildingenergy.substation_manager.meter.model;

import com.buildingenergy.substation_manager.floor.model.Floor;
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
@Entity(name = "meters")
public class Meter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String meterName;

    @Column(nullable = false)
    private String outsideBody;

    private String room;

    private String description;

    private BigDecimal energyPercentage;

    private BigDecimal oldReadings;

    private BigDecimal newReadings;

    private BigDecimal differenceReadings;

    private BigDecimal totalCost;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    @ManyToOne(optional = false)
    private Floor floor;

    @ManyToOne(optional = false)
    private User user;

}
