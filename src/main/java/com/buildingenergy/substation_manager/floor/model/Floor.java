package com.buildingenergy.substation_manager.floor.model;

import com.buildingenergy.substation_manager.meter.model.Meter;
import com.buildingenergy.substation_manager.company.model.Company;
import com.buildingenergy.substation_manager.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "floors")
public class Floor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int floorNumber;

    @ManyToOne(optional = false)
    private User user;

}
