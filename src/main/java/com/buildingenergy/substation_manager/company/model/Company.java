package com.buildingenergy.substation_manager.company.model;

import com.buildingenergy.substation_manager.floor.model.Floor;
import com.buildingenergy.substation_manager.reading.model.Reading;
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
@Entity(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false)
    private User user;

    @OneToMany(mappedBy = "company")
    private List<Reading> readings;

    @ManyToOne(optional = false)
    private Floor floor;

}
