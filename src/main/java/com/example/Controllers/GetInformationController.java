package com.example.Controllers;


import com.example.Entity.*;
import com.example.Repository.*;
import com.example.loger.Loggable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/MovieDetails")
public class GetInformationController {

    private final MoviesRepo moviesRepo;
    private final ScriptRepo scriptRepo;
    private final FinanceRepo financeRepo;
    private final ShootingDayRepo shootingDayRepo;
    private final ActorRepo actorRepo;
    private final CrewMemberRepo crewMemberRepo;

//    @Loggable
//    @GetMapping("/detalsForFilms/{id}")
//    @PreAuthorize("hasAuthority('ROLE_USER')")
//    public String GetFilms(Model model,  @PathVariable Long id) {
//        Optional<Movies> optionalFilm = moviesRepo.findById(id);
//
//        optionalFilm.ifPresent(film -> model.addAttribute("film", film));
//
//     List<Finance> finances = financeRepo.findByMovieId(id);
//        model.addAttribute("finances", finances);
//
//        List<Script> script = scriptRepo.findByMovieId(id);
//        model.addAttribute("script", script);
//
//        List<ShootingDay> shootingDays = shootingDayRepo.findByMovieId(id);
//        model.addAttribute("shootingDays", shootingDays);
//
//        List<Actors> actors = actorRepo.findMovieWithActorsById(id);
//        model.addAttribute("actors", actors);
//
//        List<FilmCrewMembers> crewMembers = crewMemberRepo.findCrewMembersByMovieId(id);
//        model.addAttribute("crewMembers", crewMembers);
//        return "detalsForFilms";
//    }

    @Loggable
    @GetMapping("/GetInformation")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String getFilmInformation(Model model) {
        return "GetInformation";
    }
}
