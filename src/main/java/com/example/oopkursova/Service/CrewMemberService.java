package com.example.oopkursova.Service;

import com.example.oopkursova.Entity.FilmCrewMembers;
import com.example.oopkursova.Repository.CrewMemberRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class CrewMemberService {
    private final CrewMemberRepo crewMemberRepo;

    public List<FilmCrewMembers> allGetCrewMember(){
        return crewMemberRepo.findAllWithMovies();
    }
    @Transactional
    public FilmCrewMembers createdCrewMember(FilmCrewMembers filmCrewMembers){
        return crewMemberRepo.save(filmCrewMembers);
    }
}
