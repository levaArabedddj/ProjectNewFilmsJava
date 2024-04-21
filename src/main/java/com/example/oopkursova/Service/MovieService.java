package com.example.oopkursova.Service;

import com.example.oopkursova.Entity.Movies;
import com.example.oopkursova.Repository.MoviesRepo;
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

    public Movies createdMovies(Movies movies){
        return moviesRepo.save(movies);
    }
}
