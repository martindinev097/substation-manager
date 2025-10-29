package com.buildingenergy.substation_manager.security;

import com.buildingenergy.substation_manager.user.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class UserData implements UserDetails {

    private UUID userId;
    private String username;
    private String password;
    private UserRole role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority role = new SimpleGrantedAuthority("ROLE_" + this.role.name());

        return List.of(role);
    }
}
