package com.buildingenergy.substation_manager.user.repository;

import com.buildingenergy.substation_manager.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findUserByUsername(String username);

    Optional<User> findByEmail(String email);
}
