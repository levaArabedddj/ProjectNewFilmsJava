package com.example.oopkursova.Controllers;

import com.example.oopkursova.Entity.FilmCrewMembers;
import com.example.oopkursova.Service.CrewMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CrewMemberControllers {
    private final CrewMemberService crewMemberService;

    public CrewMemberControllers(CrewMemberService crewMemberService) {
        this.crewMemberService = crewMemberService;
    }

    @GetMapping("/filmcrew/allWithMovies")
    public ResponseEntity<List<FilmCrewMembers>> getAllFilmCrewMembersWithMovies() {
        List<FilmCrewMembers> filmCrewMembers = crewMemberService.allGetCrewMember();
        return ResponseEntity.ok().body(filmCrewMembers);
    }

    @PostMapping("/created/MemberService")
    public ResponseEntity<FilmCrewMembers> createActors(@RequestBody FilmCrewMembers crewMembers){
        FilmCrewMembers createdActors = crewMemberService.createdCrewMember(crewMembers);
        return ResponseEntity.ok().body(createdActors);
    }
}
