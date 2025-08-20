package com.example.config;

import com.example.DTO.UserCacheDTO;
import com.example.Entity.Users;
import com.example.Enum.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyUserDetails implements UserDetails, Serializable  {

    private long user_id;
    private String userName;
    private UserRole role;
    private String gmail;

    private String password;

    public static MyUserDetails fromDTO(UserCacheDTO dto) {
        return new MyUserDetails(
                dto.getUserId(),
                dto.getUsername(),
                UserRole.valueOf(dto.getRole()),
                dto.getGmail(),
                dto.getPassword()
        );
    }


    public static MyUserDetails build(Users user) {

        return new MyUserDetails(
                user.getUser_id(),
                user.getUserName(),
                user.getRole(),
                user.getGmail(),
                user.getPassword());
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}

