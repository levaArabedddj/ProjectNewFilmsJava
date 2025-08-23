package com.example.config;

import com.example.DTO.UserCacheDTO;
import com.example.Entity.Users;
import com.example.Enum.UserRole;
import io.jsonwebtoken.Claims;
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

    public static MyUserDetails fromClaims(Claims claims) {
        MyUserDetails userDetails = new MyUserDetails();

        // username (subject)
        String subject = claims.getSubject();
        userDetails.setUserName(subject);

        // user_id
        Object idObject = claims.get("userId");
        if (idObject != null) {
            if (idObject instanceof Number) {
                userDetails.setUser_id(((Number) idObject).longValue());
            } else {
                try {
                    userDetails.setUser_id(Long.parseLong(idObject.toString()));
                } catch (NumberFormatException e) {
                }
            }
        }

        // role
        Object roleObj = claims.get("role");
        if (roleObj == null) {
            Object rolesObj = claims.get("roles");
            if (rolesObj instanceof java.util.List<?> && !((java.util.List<?>) rolesObj).isEmpty()) {
                roleObj = ((java.util.List<?>) rolesObj).get(0);
            }
        }
        if (roleObj != null) {
            try {
                userDetails.setRole(UserRole.valueOf(roleObj.toString()));
            } catch (IllegalArgumentException ex) {
            }
        }

        // gmail / email
        Object gmailObj = claims.get("gmail");
        if (gmailObj == null) {
            gmailObj = claims.get("email");
        }
        if (gmailObj != null) {
            userDetails.setGmail(gmailObj.toString());
        }

        userDetails.setPassword(null);

        return userDetails;
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

