package com.buildingenergy.substation_manager.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class CompanyView {

    private UUID id;
    private String name;
    private int floorNumber;
    private double totalConsumption;

}
