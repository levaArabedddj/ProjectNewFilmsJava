package com.example.config;

import com.example.DTO.UserCacheDTO;
import com.example.Service.UserCacheService;
import io.jsonwebtoken.Claims;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TokenFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(TokenFilter.class);

    private final JwtCore jwtCore;
    private final UserCacheService userCacheService;
    private final CacheManager cacheManager;
    private static final Logger timingLogger = LoggerFactory.getLogger("TimingLogger");


    public TokenFilter(@NonNull JwtCore jwtCore,
                       @NonNull UserCacheService userCacheService,
                       CacheManager cacheManager) {
        this.jwtCore = jwtCore;
        this.userCacheService = userCacheService;
        this.cacheManager = cacheManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        long startTime = System.currentTimeMillis();
        String path = request.getRequestURI();

        if (path.startsWith("/auth/") || path.startsWith("/oauth2/") || path.startsWith("/login/oauth2/") ||
                "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String header = request.getHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
                return;
            }

            String token = header.substring(7);
            if (!jwtCore.isValidToken(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                return;
            }

            Claims claims = jwtCore.getAllClaimsFromToken(token);
            String username = claims.getSubject();
            if (username == null || username.isBlank()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token (no subject)");
                return;
            }

            // Попытка взять DTO из кеша
            Cache cache = cacheManager != null ? cacheManager.getCache("users") : null;
            UserCacheDTO dto = null;
            if (cache != null) {
                dto = cache.get(username, UserCacheDTO.class);
                if (dto != null) {
                    log.info("User '{}' found in cache", username);
                }
            } else {
                log.warn("CacheManager returned null cache or cache 'users' not registered");
            }

            // Если нет в кэше — загрузим и положим
            if (dto == null) {
                log.info("User '{}' not found in cache, loading from DB", username);
                dto = userCacheService.loadUserCacheDTO(username);
                if (dto == null) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
                    return;
                }
                if (cache != null) {
                    cache.put(username, dto);
                    log.info("User '{}' saved to cache", username);
                }
            }

            // Локальный MyUserDetails из DTO
            MyUserDetails userDetails = MyUserDetails.fromDTO(dto);

            // Получаем роли: сперва из токена (claims), если нет — из DTO.role
            @SuppressWarnings("unchecked")
            List<String> rolesFromClaims = (List<String>) claims.get("roles");
            List<String> roles;
            if (rolesFromClaims != null && !rolesFromClaims.isEmpty()) {
                roles = rolesFromClaims;
            } else if (dto.getRole() != null) {
                roles = Collections.singletonList("ROLE_" + dto.getRole());
            } else {
                roles = Collections.emptyList();
            }

            List<GrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userDetails, null, authorities);


            SecurityContextHolder.getContext().setAuthentication(auth);
            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            log.error("TokenFilter error", ex);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
        } finally {
            long totalTime = System.currentTimeMillis() - startTime;
            timingLogger.info("Total request time for {} {}: {} ms",
                    request.getMethod(), request.getRequestURI(), totalTime);
        }
    }
}
