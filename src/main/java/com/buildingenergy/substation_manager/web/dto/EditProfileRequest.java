package com.buildingenergy.substation_manager.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditProfileRequest {

    @Email
    @NotBlank(message = "Email cannot be empty")
    private String email;

    private String firstName;

    private String lastName;
}
