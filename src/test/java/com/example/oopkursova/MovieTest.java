package com.example.oopkursova;
// --- Юнит-тест для MovieCreationService ---

import com.example.DTO.DtoMovie;
import com.example.Entity.Movies;
import com.example.Entity.Users;
import com.example.Entity.Director;
import com.example.Enum.DevelopmentStage;
import com.example.Enum.Genre;
import com.example.Exception.ApiException;
import com.example.Repository.MoviesRepo;
import com.example.Repository.DirectorRepo;
import com.example.Repository.UsersRepo;
import com.example.Service.MovieCreationService;
import com.example.config.MyUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class MovieTest {

    @Mock
    private MoviesRepo moviesRepo;
    @Mock
    private DirectorRepo directorRepo;
    @Mock
    private UsersRepo usersRepo;


    @InjectMocks
    private MovieCreationService creationService;

    private MyUserDetails userDetails;
    private Users user;
    private Director director;


    @BeforeEach
    void setUp() {
        userDetails = mock(MyUserDetails.class);
        when(userDetails.getUser_id()).thenReturn(1L);

        user = new Users();
        user.setUser_id(1L);
        director = new Director();
        director.setUsers(user);
    }


    @Test
    void testCreateMovie_Success() {
        Movies request = new Movies();
        request.setTitle("Test Movie");
        request.setDescription("Desc");
        request.setGenre_film(Genre.ACTION);

        when(usersRepo.findById(1L)).thenReturn(Optional.of(user));
        when(directorRepo.findByUsers(user)).thenReturn(Optional.of(director));
        when(moviesRepo.findByTitle("Test Movie")).thenReturn(Optional.empty());

        Movies saved = new Movies();
        saved.setTitle("Test Movie");
        saved.setDescription("Desc");
        saved.setGenre_film(Genre.ACTION);
        saved.setDevelopmentStage(DevelopmentStage.CONCEPT);
        saved.setDirector(director);
        saved.setDateTimeCreated(LocalDateTime.now());
        when(moviesRepo.save(any(Movies.class))).thenReturn(saved);

        DtoMovie result = null;
        try {
            result = creationService.createMovieForUser(request, userDetails);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }

        assertEquals("Test Movie", result.getTitle());
        assertEquals("Desc", result.getDescription());
        assertEquals(Genre.ACTION, result.getGenre_film());
        verify(moviesRepo).save(any());


    }



    @Test
    void testCreateMovie_UserNotFound() {
        // Arrange
        Movies request = new Movies();
        request.setTitle("Test Movie");

        when(usersRepo.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ApiException thrown = assertThrows(ApiException.class, () ->
                creationService.createMovieForUser(request, userDetails)
        );
        assertEquals("User not found", thrown.getMessage());
        verify(usersRepo).findById(1L);
        verifyNoMoreInteractions(directorRepo, moviesRepo);
    }

    @Test
    void testCreateMovie_DuplicateFilm() {
        // Arrange
        Movies request = new Movies();
        request.setTitle("Test Movie");

        when(usersRepo.findById(1L)).thenReturn(Optional.of(user));
        when(directorRepo.findByUsers(user)).thenReturn(Optional.of(director));
        when(moviesRepo.findByTitle("Test Movie")).thenReturn(Optional.of(new Movies()));

        // Act & Assert
        ApiException thrown = assertThrows(ApiException.class, () ->
                creationService.createMovieForUser(request, userDetails)
        );
        assertEquals("Film already exists", thrown.getMessage());
        verify(moviesRepo).findByTitle("Test Movie");
        verifyNoMoreInteractions(moviesRepo);
    }

    @Test
    public void testUpdateMovieSuccess() throws ApiException {
        // 1) запрос
        Movies request = new Movies();
        request.setId(1L);
        request.setTitle("Test Movie Update");
        request.setDescription("Desc Update");
        request.setGenre_film(Genre.DOCUMENTARY);


        when(usersRepo.findById(1L)).thenReturn(Optional.of(user));
        when(directorRepo.findByUsers(user)).thenReturn(Optional.of(director));

        var id = moviesRepo.findById(1L);
        when(id).thenReturn(Optional.empty());

        Movies saved = new Movies();
        saved.setTitle("Test Movie Update");
        saved.setDescription("Desc Update");
        saved.setGenre_film(Genre.ACTION);
        saved.setDevelopmentStage(DevelopmentStage.CONCEPT);
        saved.setDirector(director);
        saved.setDateTimeCreated(LocalDateTime.now());
        when(moviesRepo.save(any(Movies.class))).thenReturn(saved);

        // 5) вызов
        DtoMovie result = creationService.updateMovieForUser(request, 1L, userDetails);

        // 6) проверки
        assertEquals("Test Movie Update", result.getTitle());
        assertEquals("Desc Update", result.getDescription());
        assertEquals(Genre.ACTION, result.getGenre_film());
        verify(moviesRepo).save(any());
        System.out.println(result);
        System.out.println(request);

    }

    @Test
    void whenUserNotFound_thenThrowApiException() {
        Movies request = new Movies();
        // 1) мокируем, что пользователь не найден
        when(usersRepo.findById(1L)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () ->
                creationService.updateMovieForUser(request, 1L, userDetails)
        );
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void whenNotADirector_thenThrowApiException() {
        Movies request = new Movies();
        // 1) юзер найден
        when(usersRepo.findById(1L)).thenReturn(Optional.of(user));
        // 2) но не директор
        when(directorRepo.findByUsers(user)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () ->
                creationService.updateMovieForUser(request, 1L, userDetails)
        );
        assertEquals("You are not a director", ex.getMessage());
    }


    @Test
    void deleteMovieSuccess() throws ApiException {
        // 1) Подготовка «запроса»
        Movies request = new Movies();
        request.setId(1L);
        request.setTitle("Test Movie");
        request.setDescription("Desc Update");
        request.setGenre_film(Genre.ACTION);
        request.setDevelopmentStage(DevelopmentStage.CONCEPT);

        // 2) Моки для пользователя и режиссёра
        when(usersRepo.findById(1L)).thenReturn(Optional.of(user));
        when(directorRepo.findByUsers(user)).thenReturn(Optional.of(director));

        // 3) Стаб для того, чтобы репозиторий «нашёл» фильм
        when(moviesRepo.findById(1L)).thenReturn(Optional.of(request));

        // 4) Вызов тестируемого метода
        var result = creationService.movieDelete(request, userDetails);

        // 5) Проверки
        // После удаления вы ожидаете пустой DtoMovie (null-поля)
        assertNull(result.getTitle(),       "Title должен быть null после удаления");
        assertNull(result.getDescription(), "Description должен быть null после удаления");

        // И проверяем, что действительно вызвался delete(movie)
        verify(moviesRepo).delete(request);
    }


    @Test
    void deleteMovie_UserNotFound_ThrowsApiException() {
        // 1) Подготовка «запроса»
        Movies request = new Movies();
        request.setId(1L);

        // 2) Мок: пользователь не найден
        when(usersRepo.findById(1L)).thenReturn(Optional.empty());

        // 3) Выполняем и проверяем
        ApiException ex = assertThrows(ApiException.class, () ->
                creationService.movieDelete(request, userDetails)
        );
        assertEquals("User not found", ex.getMessage());

        // Репозиторий delete при этом не должен вызываться
        verify(moviesRepo, never()).delete(any());
    }

    @Test
    void deleteMovie_MovieNotFound_ThrowsApiException() {
        // 1) Подготовка «запроса»
        Movies request = new Movies();
        request.setId(42L);

        // 2) Пользователь и режиссёр прошли
        when(usersRepo.findById(1L)).thenReturn(Optional.of(user));
        when(directorRepo.findByUsers(user)).thenReturn(Optional.of(director));

        // 3) Фильм с таким ID отсутствует
        when(moviesRepo.findById(42L)).thenReturn(Optional.empty());

        // 4) Выполняем и проверяем
        ApiException ex = assertThrows(ApiException.class, () ->
                creationService.movieDelete(request, userDetails)
        );
        assertEquals("Movie not found", ex.getMessage());

        // Репозиторий delete при этом не должен вызываться
        verify(moviesRepo, never()).delete(any());
    }



}



