package com.buildingenergy.substation_manager.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MeterRequest {

    @NotBlank
    private String meterName;

    @NotBlank
    private String outsideBody;

    @NotBlank
    private String room;

    private String description;

}
