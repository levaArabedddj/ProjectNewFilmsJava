package com.example.oopkursova.Controllers;

import com.example.oopkursova.Entity.Actors;
import com.example.oopkursova.loger.Loggable;
import com.example.oopkursova.Repository.ActorRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class AddActorsControllers {

    private final ActorRepo actorRepo;
    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(AddActorsControllers.class);



    public AddActorsControllers(ActorRepo actorRepo, EntityManager entityManager) {
        this.actorRepo = actorRepo;
        this.entityManager = entityManager;
    }

    @Loggable
    @GetMapping("/ActorsOnPlayFilm")
    public String GetFormFinance(Model model){

        List<Actors> actors = actorRepo.findAll();
        model.addAttribute("actors", actors);
        model.addAttribute("actorse", new Actors());
        return "ActorsOnPlayFilm";
    }


    @Loggable
    @PostMapping("/ActorsOnPlayFilm")
    @Transactional
    public ModelAndView addActorToMovie(
            @RequestParam long actorId,
            @RequestParam long movieId) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            // Создаем запись в ассоциативной таблице actors_movies
            String sql = "INSERT INTO actors_movies (actors_id, movie_id) VALUES (:actorId, :movieId)";
            entityManager.createNativeQuery(sql)
                    .setParameter("actorId", actorId)
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
