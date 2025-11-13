package com.buildingenergy.substation_manager.web.dto;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EditProfileRequest {

    @Email
    private String email;

    private String firstName;

    private String lastName;
}
