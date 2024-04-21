package com.example.oopkursova.Service;

import com.example.oopkursova.Entity.Actors;
import com.example.oopkursova.Repository.ActorRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class ActorsService {
    @Autowired
    public final ActorRepo actorRepo;


    @Transactional
    public Actors CreatActors(Actors actors){
        return actorRepo.save(actors);
    }
}
