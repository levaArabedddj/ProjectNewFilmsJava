package com.example.oopkursova.config;
import com.example.oopkursova.Entity.UserRole;
import com.example.oopkursova.Entity.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
@AllArgsConstructor
public class MyUserDetails implements UserDetails {

    private long user_id;
    private String name;
    private UserRole role;
    private String gmail;
    private String password;


    public static MyUserDetails build(Users user) {

        return new MyUserDetails(
                user.getUser_id(),
                user.getName(),
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
        return name;
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

