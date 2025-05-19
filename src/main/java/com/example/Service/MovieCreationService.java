package com.example.Service;


import com.example.DTO.DtoMovie;
import com.example.Entity.Director;
import com.example.Entity.Movies;
import com.example.Entity.Users;
import com.example.Enum.DevelopmentStage;
import com.example.Exception.ApiException;
import com.example.Repository.DirectorRepo;
import com.example.Repository.MoviesRepo;
import com.example.Repository.UsersRepo;
import com.example.config.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MovieCreationService {

    private final MoviesRepo moviesRepo;
    private final DirectorRepo directorRepo;
    private final UsersRepo usersRepo;

    @Autowired
    public MovieCreationService(MoviesRepo moviesRepo,
                                DirectorRepo directorRepo,
                                UsersRepo usersRepo) {
        this.moviesRepo = moviesRepo;
        this.directorRepo = directorRepo;
        this.usersRepo = usersRepo;
    }

    public DtoMovie createMovieForUser(Movies movies, MyUserDetails currentUser) throws ApiException {
        Long userId = currentUser.getUser_id();

        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new ApiException("User not found"));

        Director director = directorRepo.findByUsers(user)
                .orElseThrow(() -> new ApiException("You are not a director"));

        if (moviesRepo.findByTitle(movies.getTitle()).isPresent()) {
            throw new ApiException("Film already exists");
        }

        Movies newMovie = new Movies();
        newMovie.setTitle(movies.getTitle());
        newMovie.setDescription(movies.getDescription());
        newMovie.setGenre_film(movies.getGenre_film());
        newMovie.setDevelopmentStage(DevelopmentStage.CONCEPT);
        newMovie.setDirector(director);

        Movies saved = moviesRepo.save(newMovie);

        DtoMovie dto = new DtoMovie();
        dto.setTitle(saved.getTitle());
        dto.setDescription(saved.getDescription());
        dto.setGenre_film(saved.getGenre_film());
        dto.setDateTimeCreated(saved.getDateTimeCreated());

        return dto;
    }

    public DtoMovie updateMovieForUser(Movies movies, Long movieId, MyUserDetails currentUser) throws ApiException {
        Long userId = currentUser.getUser_id();

        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new ApiException("User not found"));

        Director director = directorRepo.findByUsers(user)
                .orElseThrow(() -> new ApiException("You are not a director"));


        if(moviesRepo.findById(movies.getId()).isPresent()) {
            throw new ApiException("Movie not found");
        }

        Movies updateMovie = new Movies();
        updateMovie.setTitle(movies.getTitle());
        updateMovie.setDescription(movies.getDescription());
        updateMovie.setGenre_film(movies.getGenre_film());
        updateMovie.setDevelopmentStage(DevelopmentStage.CONCEPT);
        updateMovie.setDirector(director);

        Movies saved = moviesRepo.save(updateMovie);

        DtoMovie dto = new DtoMovie();
        dto.setTitle(saved.getTitle());
        dto.setDescription(saved.getDescription());
        dto.setGenre_film(saved.getGenre_film());
        dto.setDateTimeCreated(saved.getDateTimeCreated());

        return dto;

    }


    public DtoMovie movieDelete(Movies movies, MyUserDetails currentUser) throws ApiException {
        Long userId = currentUser.getUser_id();

        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new ApiException("User not found"));

        Director director = directorRepo.findByUsers(user)
                .orElseThrow(() -> new ApiException("You are not a director"));

        Movies movie = moviesRepo.findById(movies.getId())
                .orElseThrow(() -> new ApiException("Movie not found"));


        moviesRepo.delete(movies);
        DtoMovie dtoMovie = new DtoMovie();
        return dtoMovie;
    }
}
