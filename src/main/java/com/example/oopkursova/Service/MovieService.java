package com.example.oopkursova.Service;

import com.example.oopkursova.Entity.Movies;
import com.example.oopkursova.Repository.MoviesRepo;
import com.example.oopkursova.loger.Loggable;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class MovieService {
    @Autowired
    private final MoviesRepo moviesRepo;
    @Loggable
    public Movies findById(long id) {
        return moviesRepo.findById(id);
    }
    @Loggable
    public void save(Movies movie) {
        moviesRepo.save(movie);
    }
    @Loggable
    public Movies createdMovies(Movies movies){
        return moviesRepo.save(movies);
    }

    @Loggable
    public void update(Movies movie) {
        moviesRepo.save(movie);
    }

    @Loggable
    public void deleteMovie(Long id ){
        moviesRepo.deleteById(id);
    }

}
