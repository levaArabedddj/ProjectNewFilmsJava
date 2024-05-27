package com.example.oopkursova.Controllers;

import com.example.oopkursova.Entity.FilmCrewMembers;
import com.example.oopkursova.loger.Loggable;
import com.example.oopkursova.Repository.CrewMemberRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/CrewMember")
public class AddCrewMemberController {

    private final CrewMemberRepo crewMemberRepo;

    @PersistenceContext
    private EntityManager entityManager;

    public AddCrewMemberController(CrewMemberRepo crewMemberRepo) {
        this.crewMemberRepo = crewMemberRepo;
    }

    @Loggable
    @GetMapping("/CrewMemberOnPlayFilm")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String GetFormFinance(Model model){

        List<FilmCrewMembers> crewMembers = crewMemberRepo.findAll();
        model.addAttribute("crewMember", crewMembers);
        return "CrewMemberOnPlayFilm";
    }
    @Loggable
    @PostMapping("/CrewMemberOnPlayFilm")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Transactional
    public ModelAndView addActorToMovie(
            @RequestParam long crewMemberId,
            @RequestParam long movieId) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            // Создаем запись в ассоциативной таблице actors_movies
            String sql = "INSERT INTO public.film_crew_member_movies (film_crew_member_id, movie_id) VALUES (:crewMemberId, :movieId)";
            entityManager.createNativeQuery(sql)
                    .setParameter("crewMemberId", crewMemberId)
                    .setParameter("movieId", movieId)
                    .executeUpdate();

            modelAndView.setViewName("MenuDirectors"); // Перенаправляем на страницу успешного добавления
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.setViewName("MenuDirectors"); // Перенаправляем на страницу ошибки
        }
        return modelAndView;
    }


}
