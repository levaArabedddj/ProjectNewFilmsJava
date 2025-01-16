package com.example.oopkursova.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;@Component
public class TokenFilter extends OncePerRequestFilter {

    private final JwtCore jwtCore;
    private final MyUserDetailsService myUserDetailsService;

    @Autowired
    public TokenFilter(@NonNull JwtCore jwtCore, @NonNull MyUserDetailsService myUserDetailsService) {
        this.jwtCore = jwtCore;
        this.myUserDetailsService = myUserDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String jwt = null;
        String name = null;
        UserDetails userDetails;
        UsernamePasswordAuthenticationToken authenticationToken;
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer")) {
                jwt = authHeader.substring(7);
            }
            if (jwt != null && jwtCore.isValidToken(jwt)) { // Вызов через экземпляр
                try {
                    name = jwtCore.getNameFromToken(jwt); // Вызов через экземпляр
                } catch (ExpiredJwtException e) {
                    System.out.println(e.getMessage());
                }
                if (name != null) {
                    userDetails = myUserDetailsService.loadUserByUsername(name);
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    } else {
                        if (!userDetails.isAccountNonLocked()) {
                            return;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
