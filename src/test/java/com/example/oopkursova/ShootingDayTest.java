package com.example.oopkursova;

import com.example.DTO.DtoShootingDay;
import com.example.Entity.Director;
import com.example.Entity.MoviesPackage.Finance;
import com.example.Entity.MoviesPackage.Movies;
import com.example.Entity.Users;
import com.example.Exception.ApiException;
import com.example.Repository.*;
import com.example.Service.ShootingDayService;
import com.example.config.MyUserDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShootingDayTest {

    private MyUserDetails userDetails;
    private Users user;
    private Director director;
    @Mock private MoviesRepo moviesRepo;

    @Mock
    private DirectorRepo directorRepo;
    @Mock private UsersRepo usersRepo;
    @InjectMocks private ShootingDayService service;
    @Mock private ShootingDayRepo shootingDayRepo;

    private Finance existing;

//    @BeforeEach
//    void setUp() {
//        userDetails = mock(MyUserDetails.class);
//        when(userDetails.getUser_id()).thenReturn(1L);
//
//        user = new Users();
//        user.setUser_id(1L);
//        director = new Director();
//        director.setUsers(user);
//    }
    @Test
    void testCreateShootingDay_Success() {
        Long filmId = 1L;
        LocalDate date = LocalDate.of(2025, 5, 30);
        DtoShootingDay dto = new DtoShootingDay();
        dto.setShootingDate(date);
        dto.setShootingTime(LocalTime.parse("10:00"));
        dto.setShootingLocation("Kyiv");
        dto.setEstimatedDurationHours(5);

        Movies movie = new Movies();
        movie.setId(filmId);


        when(shootingDayRepo.existsByShootingDateAndMovieId(LocalDate.from(date), filmId)).thenReturn(false); // если ты неправильно указал repo — поправь
        when(moviesRepo.findById(filmId)).thenReturn(Optional.of(movie));


        DtoShootingDay result = service.createShootingDay(filmId, dto);


        assertNotNull(result);
        assertEquals(LocalTime.of(10, 0), result.getShootingTime());
        assertEquals("Kyiv", result.getShootingLocation());
        assertEquals(5, result.getEstimatedDurationHours());
    }

    @Test
    void testCreateShootingDay_Failure_ShootingDayAlreadyExists() {

        Long filmId = 1L;
        LocalDate date = LocalDate.of(2025, 5, 30);

        DtoShootingDay dto = new DtoShootingDay();
        dto.setShootingDate(date);
        dto.setShootingTime(LocalTime.of(10, 0));
        dto.setShootingLocation("Kyiv");
        dto.setEstimatedDurationHours(5);


        when(shootingDayRepo.existsByShootingDateAndMovieId(date, filmId)).thenReturn(true);

        ApiException thrown = assertThrows(ApiException.class, () -> {
            service.createShootingDay(filmId, dto);
        });

        assertEquals("Shooting already exists on this day", thrown.getMessage());
    }

    @Test
    void testCreateShootingDay_Failure_FilmNotFound() {
        Long filmId = 999L;
        LocalDate date = LocalDate.of(2025, 6, 1);

        DtoShootingDay dto = new DtoShootingDay();
        dto.setShootingDate(date);
        dto.setShootingTime(LocalTime.of(12, 0));
        dto.setShootingLocation("Lviv");
        dto.setEstimatedDurationHours(3);


        when(shootingDayRepo.existsByShootingDateAndMovieId(date, filmId)).thenReturn(false);
        when(moviesRepo.findById(filmId)).thenReturn(Optional.empty());

        ApiException thrown = assertThrows(ApiException.class, () -> {
           service .createShootingDay(filmId, dto);
        });

        assertEquals("Film not found", thrown.getMessage());
    }



}
