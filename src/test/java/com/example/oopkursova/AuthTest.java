package com.example.oopkursova;


import com.example.Controllers.SecurityController;
import com.example.ElasticSearch.Service.ActorService;
import com.example.ElasticSearch.Service.CrewMemberServiceElastic;
import com.example.Entity.Users;
import com.example.Enum.Gender;
import com.example.Enum.UserRole;
import com.example.Repository.*;
import com.example.Service.CrewMemberService;
import com.example.Service.SenderService;
import com.example.config.JwtCore;
import com.example.config.MyUserDetailsService;
import com.example.config.SignupRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;


@WebMvcTest(controllers = SecurityController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UsersRepo usersRepo;
    @MockBean
    private ActorRepo actorRepo;
    @MockBean
    private ActorProfilesRepository actorProfilesRepository;
    @MockBean
    private ActorService actorService;
    @MockBean
    private MyUserDetailsService myUserDetailsService;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private JwtCore jwtCore;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private  CrewMemberRepo crewMemberRepo;
    @MockBean
    private   CrewMemberProfilesRepo crewMemberProfilesRepo;
    @MockBean
    private  SenderService emailService;
    @MockBean
    private  DirectorRepo directorRepo;
    @MockBean
    private  DirectorProfilesRepo directorProfilesRepo;
    @MockBean
    private CrewMemberService service;
    @MockBean
    private CrewMemberServiceElastic serviceElastic;

    @MockBean
    private MoviesRepo moviesRepo;

    @MockBean
    private VisitorRepo visitorRepo;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean private AdminRepo adminRepo;


    @Test
    void signup_whenUsernameExists_thenBadRequest() throws Exception {
        // подготовка
        SignupRequest req = new SignupRequest();
        req.setUserName("bob");
        req.setGmail("bob@example.com");
        req.setPassword("pass");
        req.setRole(UserRole.VISITOR);
        req.setName("Bob");
        req.setSurName("Builder");
        req.setGender(Gender.MALE);
        req.setPhone("123");
        when(usersRepo.existsUsersByUserName("bob")).thenReturn(true);

        mockMvc.perform(post("/auth/signup-Login")
                        .contentType(APPLICATION_JSON)
                        .content(asJson(req)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Choose different name"));
    }


    @Test
    void signup_whenEmailExists_thenBadRequest() throws Exception {
        // GIVEN
        SignupRequest req = new SignupRequest();
        req.setUserName("alice");
        req.setGmail("alice@example.com");
        req.setPassword("pass");
        req.setRole(UserRole.VISITOR);
        req.setName("Alice");
        req.setSurName("Wonder");
        req.setGender(Gender.FEMALE);
        req.setPhone("456");

        when(usersRepo.existsUsersByUserName("alice")).thenReturn(false);
        when(usersRepo.existsUsersByGmail("alice@example.com")).thenReturn(true);

        // WHEN / THEN
        mockMvc.perform(post("/auth/signup-Login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(req)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Choose different email"));
    }

    @Test
    void signup_successful_thenReturnsJwt() throws Exception {
        // GIVEN
        SignupRequest req = new SignupRequest();
        req.setUserName("charlie");
        req.setGmail("charlie@example.com");
        req.setPassword("pass");
        req.setRole(UserRole.VISITOR);
        req.setName("Charlie");
        req.setSurName("Chaplin");
        req.setGender(Gender.MALE);
        req.setPhone("789");

        when(usersRepo.existsUsersByUserName("charlie")).thenReturn(false);
        when(usersRepo.existsUsersByGmail("charlie@example.com")).thenReturn(false);

        Users saved = new Users();
        saved.setUserName("charlie");
        saved.setGmail("charlie@example.com");
        when(usersRepo.save(any(Users.class))).thenReturn(saved);

        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(mock(Authentication.class));

        when(jwtCore.generateToken(any(Authentication.class)))
                .thenReturn("jwt-token");

        // WHEN / THEN
        mockMvc.perform(post("/auth/signup-Login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string("jwt-token"));
    }


    private static String asJson(Object o) {
        try { return new ObjectMapper().writeValueAsString(o); }
        catch (Exception e) { throw new RuntimeException(e); }
    }


}
