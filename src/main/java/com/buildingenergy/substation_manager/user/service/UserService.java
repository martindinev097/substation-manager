package com.buildingenergy.substation_manager.user.service;

import com.buildingenergy.substation_manager.exception.EmailAlreadyExists;
import com.buildingenergy.substation_manager.exception.PasswordsDoNotMatch;
import com.buildingenergy.substation_manager.exception.UsernameAlreadyExists;
import com.buildingenergy.substation_manager.exception.UsernameDoesNotExist;
import com.buildingenergy.substation_manager.security.UserData;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.user.model.UserRole;
import com.buildingenergy.substation_manager.user.repository.UserRepository;
import com.buildingenergy.substation_manager.web.dto.RegisterRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterRequest registerRequest) {

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
                .createdOn(LocalDateTime.now())
                .build();

        log.info("User %s has registered successfully".formatted(user.getUsername()));

        userRepository.save(user);
    }

    public User getById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new UsernameDoesNotExist("User with id: [%s] does not exist".formatted(id)));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username).orElseThrow(() -> new UsernameDoesNotExist("Username [%s] does not exist.".formatted(username)));

        return UserData.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .role(user.getRole())
                .build();
    }
}
