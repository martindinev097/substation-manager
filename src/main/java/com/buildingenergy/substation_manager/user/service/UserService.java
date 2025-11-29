package com.buildingenergy.substation_manager.user.service;

import com.buildingenergy.substation_manager.exception.*;
import com.buildingenergy.substation_manager.security.UserData;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.user.model.UserRole;
import com.buildingenergy.substation_manager.user.repository.UserRepository;
import com.buildingenergy.substation_manager.web.dto.EditProfileRequest;
import com.buildingenergy.substation_manager.web.dto.RegisterRequest;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterRequest registerRequest) {

        Optional<User> optionalUser = userRepository.findUserByUsername(registerRequest.getUsername());

        if (optionalUser.isPresent()) {
            throw new UsernameAlreadyExists("Username %s already exists.".formatted(registerRequest.getUsername()));
        }

        if (!registerRequest.getPassword().equals(registerRequest.getConfirmedPassword())) {
            throw new PasswordsDoNotMatch("Passwords do not match");
        }

        Optional<User> optionalEmailUser = userRepository.findByEmail(registerRequest.getEmail());

        if (optionalEmailUser.isPresent()) {
            throw new EmailAlreadyExists("Email [%s] is already in use.".formatted(registerRequest.getEmail()));
        }

        String username = registerRequest.getUsername().trim();
        String email = registerRequest.getEmail().trim();

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(email)
                .role(UserRole.USER)
                .isActive(true)
                .createdOn(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }

    public User getById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new UsernameDoesNotExist("User with id: [%s] does not exist".formatted(id)));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username).orElseThrow(() -> new UsernameDoesNotExist("Username [%s] does not exist.".formatted(username)));

        if (!user.isActive()) {
            throw new DisabledException("User account is inactive");
        }

        return UserData.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .role(user.getRole())
                .isActive(user.isActive())
                .build();
    }

    public List<User> findAll() {
        return userRepository.findAll().stream()
                .sorted(Comparator.comparing((User u) -> u.getRole() == UserRole.ADMIN ? 0 : 1))
                .toList();
    }

    public void changeStatus(User user, User admin) {
        if (user.getRole() == UserRole.ADMIN) {
            throw new CannotChangeAdminStatus("Admin status can't be changed");
        }

        user.setActive(!user.isActive());

        userRepository.save(user);
    }

    public void updateProfile(User u, EditProfileRequest editProfileRequest) {
        Optional<User> optionalEmailUser = userRepository.findByEmail(editProfileRequest.getEmail());

        if (optionalEmailUser.isPresent() && !u.getUsername().equals(optionalEmailUser.get().getUsername())) {
            throw new EmailAlreadyExists("Email [%s] is already in use.".formatted(editProfileRequest.getEmail()));
        }

        u.setEmail(editProfileRequest.getEmail());
        u.setFirstName(editProfileRequest.getFirstName());
        u.setLastName(editProfileRequest.getLastName());

        userRepository.save(u);
    }

    public User updateRole(UUID id, User currentUser) {
        User targetUser = getById(id);

        if (targetUser.getRole() == UserRole.ADMIN) {
            targetUser.setRole(UserRole.USER);

            userRepository.save(targetUser);

            if (targetUser.getId().equals(currentUser.getId())) {
                throw new ForbiddenAccessAfterRoleChange("Your role has been changed. Please log in again.");
            }
        } else if (targetUser.getRole() == UserRole.USER) {
            targetUser.setRole(UserRole.ADMIN);

            userRepository.save(targetUser);
        }

        return targetUser;
    }

    public List<User> findAllAdmins() {
        return getAll().stream().filter(user -> user.getRole() == UserRole.ADMIN).toList();
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }
}
