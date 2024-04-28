package com.example.oopkursova.Controllers;

import com.example.oopkursova.Entity.*;
import com.example.oopkursova.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class GetInformationController {

    private final MoviesRepo moviesRepo;
    private final ScriptRepo scriptRepo;
    private final FinanceRepo financeRepo;
    private final ShootingDayRepo shootingDayRepo;
    private final ActorRepo actorRepo;
    private final CrewMemberRepo crewMemberRepo;

    @GetMapping("/detalsForFilms/{id}")
    public String GetFilms(Model model,  @PathVariable Long id) {
        Optional<Movies> optionalFilm = moviesRepo.findById(id);

        optionalFilm.ifPresent(film -> model.addAttribute("film", film));

     List<Finance> finances = financeRepo.findByMovieId(id);
        model.addAttribute("finances", finances);

        List<Script> script = scriptRepo.findByMovieId(id);
        model.addAttribute("script", script);

        List<ShootingDay> shootingDays = shootingDayRepo.findByMovieId(id);
        model.addAttribute("shootingDays", shootingDays);

        List<Actors> actors = actorRepo.findMovieWithActorsById(id);
        model.addAttribute("actors", actors);

        List<FilmCrewMembers> crewMembers = crewMemberRepo.findCrewMembersByMovieId(id);
        model.addAttribute("crewMembers", crewMembers);
        return "detalsForFilms";
    }

    @GetMapping("/GetInformation")
    public String getFilmInformation(Model model) {
        // Ваш код для получения информации о фильме и его деталях
        return "GetInformation"; // Возвращаем имя вашего HTML шаблона для отображения информации о фильме
    }
}
