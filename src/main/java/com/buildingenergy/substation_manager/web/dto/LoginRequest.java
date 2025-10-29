package com.buildingenergy.substation_manager.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotBlank
    @Size(min = 4, max = 24, message = "Username must be between 4 and 24 characters.")
    private String username;

    @NotBlank
    @Size(min = 4, message = "Password must be at least 4 characters.")
    private String password;

}
