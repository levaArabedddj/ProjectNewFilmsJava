package com.example.config;

import com.example.Entity.Users;
import com.example.Service.OAuth2TokenService;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.*;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;





@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {



    @Autowired
    private TokenFilter tokenFilter;
    private MyUserDetailsService userService;
    private final MyUserDetailsService uds;
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);



    @Value("${GOOGLE_APPLICATION_CREDENTIALS}")
    String password;

    @Autowired
    public SecurityConfig(MyUserDetailsService userService, TokenFilter authTokenFilter, MyUserDetailsService uds) {
        this.userService = userService;
        this.tokenFilter = authTokenFilter;
        this.uds = uds;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return new MyUserDetailsService();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    @Primary
    public AuthenticationManagerBuilder configirareAuthicationManagerBuilder(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
        return auth;
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }



    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(List.of("http://localhost:5173")); // Разрешённый Origin
        corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }
    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter(corsConfigurationSource()); // Используем правильный CorsFilter
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,AuthenticationSuccessHandler oAuth2SuccessHandler,
                                            AuthenticationFailureHandler oAuth2FailureHandler, OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .authorizeHttpRequests(authorizeRequests ->authorizeRequests
                        .requestMatchers("/auth/**","/oauth2/**", "/login/oauth2/**", "/oauth2/authorization/**").permitAll()
                        .dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll()
                        .requestMatchers("/secured/user").fullyAuthenticated()
                        .anyRequest().authenticated()
                )

                // Включаем OAuth2-login
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(ui -> ui
                                .oidcUserService(oidcUserService)
                        )
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)

                )



                
                .oauth2Client(Customizer.withDefaults())
                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public Storage storage() throws IOException {
        try (FileInputStream stream = new FileInputStream(password)) {
            return StorageOptions.newBuilder()
                    .setCredentials(ServiceAccountCredentials.fromStream(stream))
                    .build()
                    .getService();
        }
    }

    @Bean
    public AuthenticationSuccessHandler oAuth2SuccessHandler(JwtCore jwtCore) {
        return new SimpleUrlAuthenticationSuccessHandler() {

            @Override
            public void onAuthenticationSuccess(
                    HttpServletRequest req,
                    HttpServletResponse res,
                    Authentication auth) throws IOException {

                // 1) Извлечь OIDC-пользователя
                DefaultOidcUser oidcUser = (DefaultOidcUser) auth.getPrincipal();

                // 2) Достать email
                String email = oidcUser.getEmail();

                // 3) Найти или создать  юзера через сервис
                Users dbUser = uds.findOrCreateByEmail(email);

                // 4) Собрать MyUserDetails
                MyUserDetails userDetails = MyUserDetails.build(dbUser);

                // 5) Генерировать токен из деталей
                String token = jwtCore.generateToken(userDetails);

                // (Опционально) Вытянуть Google-access-token, если нужен
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) auth;
                OAuth2AuthorizedClient client = authorizedClientService
                        .loadAuthorizedClient(
                                oauthToken.getAuthorizedClientRegistrationId(),
                                oauthToken.getName());
                String googleAccessToken = client.getAccessToken().getTokenValue();

                // Логируем сгенерированный JWT
                log.info("Generated application JWT: {}", token);

                // Кладём JWT в заголовок ответа
                res.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

                // Отдаём JSON-ответ
                res.setStatus(HttpStatus.OK.value());
                res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                res.getWriter().write("""
                {
                  "token":"%s",
                  "google_access_token":"%s"
                }
                """.formatted(token, googleAccessToken));
            }
        };
    }



    @Bean
    public AuthenticationFailureHandler oAuth2FailureHandler() {
        // По-умолчанию просто 401 и сообщение об ошибке
        return new SimpleUrlAuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    AuthenticationException exception
            ) throws IOException, ServletException {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("{\"error\":\"" + exception.getMessage() + "\"}");
                response.getWriter().flush();
            }
        };
    }

    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        OidcUserService delegate = new OidcUserService();
        return userRequest -> {
            OidcUser oidcUser = delegate.loadUser(userRequest);
            String email = oidcUser.getEmail();
            Users dbUser = uds.findOrCreateByEmail(email);
            MyUserDetails myUser = MyUserDetails.build(dbUser);

            // Подкладываем свои детали, но сохраняем OIDC-token/info
            return new DefaultOidcUser(
                    myUser.getAuthorities(),
                    oidcUser.getIdToken(),
                    oidcUser.getUserInfo(),
                    "sub"
            );
        };
    }

}

