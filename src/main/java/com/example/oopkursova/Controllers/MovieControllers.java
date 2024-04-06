package com.example.oopkursova.Controllers;

import com.example.oopkursova.Entity.Movies;
import com.example.oopkursova.Service.MovieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class MovieControllers {
    private final MovieService movieService;

    public MovieControllers(MovieService movieService) {
        this.movieService = movieService;
    }
    @PostMapping("/add/movie")
    public ResponseEntity<Movies> createMovie(@RequestBody Movies movies){
        Movies createdMovie = movieService.createdMovies(movies);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdMovie.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdMovie);
    }
}
