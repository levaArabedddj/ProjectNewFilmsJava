package com.example.oopkursova.Controllers;

import com.example.oopkursova.Entity.Directors;
import com.example.oopkursova.Service.DirectorsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
public class DirectorsControllers {
    private final DirectorsService directorsService;

    public DirectorsControllers(DirectorsService directorsService) {
        this.directorsService = directorsService;
    }
    @GetMapping("/all")
    public ResponseEntity<List<Directors>> getAllDirectors() {
        List<Directors> directors = directorsService.getAllDirectors();
        if (directors.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(directors, HttpStatus.OK);
    }
    @PostMapping("/add/directors")
    public ResponseEntity<Directors> addDirectors(@RequestBody Directors directors){
        Directors createdDirectors = directorsService.directorsCreate(directors);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdDirectors.getAdmin_id())
                .toUri();
        return ResponseEntity.created(uri).body(createdDirectors);
    }
}
