package com.buildingenergy.substation_manager.web.mapper;

import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.web.dto.EditProfileRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DtoMapper {

    public static EditProfileRequest from(User user) {
        return EditProfileRequest.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }
}
