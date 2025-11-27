package com.buildingenergy.substation_manager.floor;

import com.buildingenergy.substation_manager.exception.FloorNotFound;
import com.buildingenergy.substation_manager.floor.model.Floor;
import com.buildingenergy.substation_manager.floor.repository.FloorRepository;
import com.buildingenergy.substation_manager.floor.service.FloorService;
import com.buildingenergy.substation_manager.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FloorServiceUTest {

    @Mock
    private FloorRepository floorRepository;

    @InjectMocks
    private FloorService floorService;

    @Test
    void givenValidFloorAndUser_whenFindByFloorNumber_thenReturnFloor() {
        User user = new User();

        Floor floor = Floor.builder()
                .floorNumber(2)
                .user(user)
                .build();

        when(floorRepository.findByFloorNumberAndUser(2, user))
                .thenReturn(Optional.of(floor));

        Floor result = floorService.findByFloorNumberAndUser(2, user);

        assertNotNull(result);
        assertEquals(2, result.getFloorNumber());

        verify(floorRepository).findByFloorNumberAndUser(2, user);
    }

    @Test
    void givenMissingFloorForUser_whenFindByFloorNumber_thenReturnNull() {
        User user = new User();

        when(floorRepository.findByFloorNumberAndUser(3, user)).thenReturn(Optional.empty());

        Floor result = floorService.findByFloorNumberAndUser(3, user);

        assertNull(result);

        verify(floorRepository).findByFloorNumberAndUser(3, user);
    }

    @Test
    void givenInvalidFloorNumber_whenFindByFloorNumber_thenThrowFloorNotFound() {
        User user = new User();

        assertThrows(FloorNotFound.class, () -> floorService.findByFloorNumberAndUser(0, user));
        assertThrows(FloorNotFound.class, () -> floorService.findByFloorNumberAndUser(6, user));

        verifyNoInteractions(floorRepository);
    }

    @Test
    void givenValidFloorNumber_whenCreateFloor_thenSaveAndReturnFloor() {
        User user = new User();

        Floor expected = Floor.builder()
                .floorNumber(4)
                .user(user)
                .build();

        when(floorRepository.save(any(Floor.class))).thenReturn(expected);

        Floor result = floorService.createFloor(4, user);

        assertNotNull(result);
        assertEquals(4, result.getFloorNumber());

        verify(floorRepository).save(any(Floor.class));
    }

    @Test
    void givenUserWithFloors_whenCountByUser_thenReturnFloorsCountForTheUser() {
        User user = new User();

        Floor.builder()
                .user(user)
                .build();

        when(floorRepository.countByUser(user)).thenReturn(1);

        int result = floorService.countByUser(user);

        assertEquals(1, result);

        verify(floorRepository).countByUser(user);
    }

}
