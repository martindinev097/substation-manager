package com.buildingenergy.substation_manager.user;

import com.buildingenergy.substation_manager.exception.*;
import com.buildingenergy.substation_manager.security.UserData;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.user.model.UserRole;
import com.buildingenergy.substation_manager.user.repository.UserRepository;
import com.buildingenergy.substation_manager.user.service.UserService;
import com.buildingenergy.substation_manager.web.dto.EditProfileRequest;
import com.buildingenergy.substation_manager.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void givenExistingUsername_whenRegister_thenThrowUsernameAlreadyExists() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("john");

        when(userRepository.findUserByUsername("john")).thenReturn(Optional.of(new User()));

        assertThrows(UsernameAlreadyExists.class, () -> userService.register(req));
    }

    @Test
    void givenMismatchedPasswords_whenRegister_thenThrowPasswordsDoNotMatch() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("john");
        req.setPassword("123");
        req.setConfirmedPassword("999");

        when(userRepository.findUserByUsername("john")).thenReturn(Optional.empty());

        assertThrows(PasswordsDoNotMatch.class, () -> userService.register(req));
    }

    @Test
    void givenExistingEmail_whenRegister_thenThrowEmailAlreadyExists() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("john");
        req.setPassword("pass");
        req.setConfirmedPassword("pass");
        req.setEmail("a@b.com");

        when(userRepository.findUserByUsername("john")).thenReturn(Optional.empty());

        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(new User()));

        assertThrows(EmailAlreadyExists.class, () -> userService.register(req));
    }

    @Test
    void givenValidRequest_whenRegister_thenSaveNewUser() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("john");
        req.setPassword("pass");
        req.setConfirmedPassword("pass");
        req.setEmail("a@b.com");

        when(userRepository.findUserByUsername("john")).thenReturn(Optional.empty());

        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.empty());

        when(passwordEncoder.encode("pass")).thenReturn("ENCODED");

        userService.register(req);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());

        User saved = captor.getValue();
        assertEquals("john", saved.getUsername());
        assertEquals("ENCODED", saved.getPassword());
        assertEquals("a@b.com", saved.getEmail());
        assertTrue(saved.isActive());
        assertEquals(UserRole.USER, saved.getRole());
    }

    @Test
    void givenExistingId_whenGetById_thenReturnUser() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        assertEquals(user, userService.getById(id));
    }

    @Test
    void givenMissingId_whenGetById_thenThrowUsernameDoesNotExist() {
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UsernameDoesNotExist.class, () -> userService.getById(id));
    }

    @Test
    void givenExistingActiveUser_whenLoadUserByUsername_thenReturnUserData() {
        User u = new User();
        u.setId(UUID.randomUUID());
        u.setUsername("john");
        u.setPassword("123");
        u.setActive(true);
        u.setRole(UserRole.USER);

        when(userRepository.findUserByUsername("john")).thenReturn(Optional.of(u));

        UserData data = (UserData) userService.loadUserByUsername("john");

        assertEquals("john", data.getUsername());
        assertEquals("123", data.getPassword());
        assertEquals(UserRole.USER, data.getRole());
        assertTrue(data.isActive());
    }

    @Test
    void givenMissingUser_whenLoadUserByUsername_thenThrowUsernameDoesNotExist() {
        when(userRepository.findUserByUsername("john")).thenReturn(Optional.empty());

        assertThrows(UsernameDoesNotExist.class, () -> userService.loadUserByUsername("john"));
    }

    @Test
    void givenInactiveUser_whenLoadUserByUsername_thenThrowDisabledException() {
        User u = new User();
        u.setActive(false);

        when(userRepository.findUserByUsername("john")).thenReturn(Optional.of(u));

        assertThrows(DisabledException.class, () -> userService.loadUserByUsername("john"));
    }

    @Test
    void givenAdminUser_whenChangeStatus_thenThrowCannotChangeAdminStatus() {
        User user = new User();
        user.setRole(UserRole.ADMIN);

        User admin = new User();

        assertThrows(CannotChangeAdminStatus.class, () -> userService.changeStatus(user, admin));
    }

    @Test
    void givenRegularUser_whenChangeStatus_thenToggleActiveAndSave() {
        User user = new User();
        user.setRole(UserRole.USER);
        user.setActive(true);

        User admin = new User();

        userService.changeStatus(user, admin);

        assertFalse(user.isActive());
        verify(userRepository).save(user);
    }

    @Test
    void givenExistingEmail_whenUpdateProfile_thenThrowEmailAlreadyExists() {
        UUID id = UUID.randomUUID();

        User u = new User();
        u.setId(id);
        u.setEmail("old@mail.com");
        u.setUsername("someone");

        EditProfileRequest req = new EditProfileRequest();
        req.setEmail("new@mail.com");

        when(userRepository.findByEmail("new@mail.com")).thenReturn(Optional.of(new User()));

        assertThrows(EmailAlreadyExists.class, () -> userService.updateProfile(u, req));
    }

    @Test
    void givenValidData_whenUpdateProfile_thenUpdateFieldsAndSave() {
        UUID id = UUID.randomUUID();

        User u = new User();
        u.setId(id);
        u.setUsername("oldUser");

        EditProfileRequest req = new EditProfileRequest();
        req.setEmail("new@mail.com");
        req.setFirstName("John");
        req.setLastName("Smith");

        when(userRepository.findByEmail("new@mail.com")).thenReturn(Optional.empty());

        userService.updateProfile(u, req);

        assertEquals("new@mail.com", u.getEmail());
        assertEquals("John", u.getFirstName());
        assertEquals("Smith", u.getLastName());

        verify(userRepository).save(u);
    }

    @Test
    void givenAdminChangingOwnRole_whenUpdateRole_thenThrowForbiddenAccessAfterRoleChangeException() {
        UUID id = UUID.randomUUID();

        User target = new User();
        target.setId(id);
        target.setRole(UserRole.ADMIN);

        User current = new User();
        current.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(target));

        assertThrows(ForbiddenAccessAfterRoleChange.class, () -> userService.updateRole(id, current));
    }

    @Test
    void givenAdminTargetAndDifferentUser_whenUpdateRole_thenDemoteToUserAndSave() {
        UUID id = UUID.randomUUID();

        User target = new User();
        target.setId(id);
        target.setRole(UserRole.ADMIN);

        User current = new User();
        current.setId(UUID.randomUUID());

        when(userRepository.findById(id)).thenReturn(Optional.of(target));

        userService.updateRole(id, current);

        assertEquals(UserRole.USER, target.getRole());

        verify(userRepository, times(1)).save(target);
    }

    @Test
    void givenRegularUser_whenUpdateRole_thenPromoteToAdminAndSave() {
        UUID id = UUID.randomUUID();

        User target = new User();
        target.setId(id);
        target.setRole(UserRole.USER);

        User current = new User();
        current.setId(UUID.randomUUID());

        when(userRepository.findById(id)).thenReturn(Optional.of(target));

        userService.updateRole(id, current);

        assertEquals(UserRole.ADMIN, target.getRole());
        verify(userRepository, times(1)).save(target);
    }

    @Test
    void givenUsersWithMixedRoles_whenFindAllAdmins_thenReturnOnlyAdmins() {
        User a1 = new User();
        a1.setRole(UserRole.ADMIN);

        User a2 = new User();
        a2.setRole(UserRole.ADMIN);

        User u1 = new User();
        u1.setRole(UserRole.USER);

        when(userRepository.findAll()).thenReturn(List.of(a1, a2, u1));

        List<User> admins = userService.findAllAdmins();

        assertEquals(2, admins.size());
    }

    @Test
    void givenAnyUsers_whenFindAll_thenReturnList() {
        User user1 = new User();
        User user2 = new User();

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        assertEquals(2, userService.findAll().size());
    }
}