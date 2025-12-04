package com.buildingenergy.substation_manager.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyView implements Serializable {

    private UUID id;
    private String name;
    private int floorNumber;
    private double totalConsumption;

}
