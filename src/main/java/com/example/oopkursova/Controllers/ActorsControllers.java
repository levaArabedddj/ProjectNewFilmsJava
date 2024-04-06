package com.example.oopkursova.Controllers;

import com.example.oopkursova.Entity.Actors;
import com.example.oopkursova.Service.ActorsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class ActorsControllers {
    private final ActorsService actorsService;

    public ActorsControllers(ActorsService actorsService) {
        this.actorsService = actorsService;
    }

    @PostMapping("/add/actors")
    public ResponseEntity<Actors> createActors(@RequestBody Actors actors){
        Actors createdActors = actorsService.CreatActors(actors);
        return ResponseEntity.ok().body(createdActors);
    }
}
