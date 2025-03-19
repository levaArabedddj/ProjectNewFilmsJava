package com.example.config;


import com.example.Repository.UsersRepo;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtCore {

    private final UsersRepo userRepository;

    @Value("${testing.app.secret}")
    public String secret;

    @Value("${testing.app.lifetime}")
    public Long lifeTime;

    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);


    @Autowired
    public JwtCore(UsersRepo userRepository) {
        this.userRepository = userRepository;
    }
    @PostConstruct
    public void ensureKeyInitialized() {
        init();
    }


    private SecretKey secretKey;

    public void init() {
        if (secret != null && secret.length() >= 32) {
            this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        } else {
            throw new IllegalArgumentException("JWT секретный ключ должен быть длиной не менее 32 символов.");
        }
    }


    public String generateToken(Authentication auth) {
        if (SECRET_KEY == null) {
            init();
        }

        MyUserDetails user = (MyUserDetails) auth.getPrincipal();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + lifeTime);

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("roles", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()))
                .claim("userId", user.getUser_id())
                .issuedAt(new Date())
                .expiration(expireDate)
                .signWith(SECRET_KEY)
                .compact();
    }

    public String getUserNameFromToken(String token) {
        if (SECRET_KEY == null) {
            init();
        }
        return Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean isValidToken(String token) {
        if (SECRET_KEY == null) {
            init();
        }
        try {
            Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parse(token);
            return true; // Токен валідний
        } catch (ExpiredJwtException e) {
            System.out.println("Токен прострочений: " + e.getMessage());
        } catch (JwtException e) {
            System.out.println("Невалідний токен: " + e.getMessage());
        }
        return false;
    }

    public boolean isValidUserToken(String token) {
        if (isValidToken(token)) {
            String username = getUserNameFromToken(token);
            if (username != null) {
                return userRepository.existsUsersByUserName(username);
            }
        }
        return false;
    }

}



