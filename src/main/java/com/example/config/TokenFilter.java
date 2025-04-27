package com.example.config;

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

import java.io.IOException;
@Component
public class TokenFilter extends OncePerRequestFilter {

    private final JwtCore jwtCore;
    private final MyUserDetailsService myUserDetailsService;

    @Autowired
    public TokenFilter(@NonNull JwtCore jwtCore, @NonNull MyUserDetailsService myUserDetailsService) {
        this.jwtCore = jwtCore;
        this.myUserDetailsService = myUserDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        // Пропускаем без проверки токена запросы на OAuth2 авторизацию и на аутентификацию
        if (path.startsWith("/auth/") || path.startsWith("/oauth2/") || path.startsWith("/login/oauth2/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = null;
        String name = null;
        UserDetails userDetails;
        UsernamePasswordAuthenticationToken authenticationToken;
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer")) {
                jwt = authHeader.substring(7);
            }
            else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
                return; // Блокуємо запит
            }



            if(jwt == null || !jwtCore.isValidToken(jwt)){
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
                return;
            }


            try {
                name = jwtCore.getUserNameFromToken(jwt); // Вызов через экземпляр
            } catch (ExpiredJwtException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
            }
            if (name != null) {
                userDetails = myUserDetailsService.loadUserByUsername(name);
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } else {
                    if (!userDetails.isAccountNonLocked()) {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Account is locked");
                        return;
                    }
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
