package com.buildingenergy.substation_manager.web.mapper;

import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.web.dto.EditProfileRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class DtoMapperUTest {

    @Test
    void givenEditProfileRequest_whenUserWithDetailsIsPassed_thenReturnWithSameDetails() {
        User user = User.builder()
                .email("test@gmail.com")
                .firstName("James")
                .lastName("Smith")
                .build();

        EditProfileRequest result = DtoMapper.from(user);

        assertEquals("test@gmail.com", result.getEmail());
        assertEquals("James", result.getFirstName());
        assertEquals("Smith", result.getLastName());
    }

}
