package org.example.servicefilm.RabbitMqUpdateMovie;

import jakarta.persistence.EntityManager;
import org.example.servicefilm.*;
import org.example.servicefilm.Entity.Director;
import org.example.servicefilm.Entity.Movies;
import org.example.servicefilm.RabbitMqService.MovieDtoRM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class FilmEventListenerUpdate {


    public static final String INDEX_EXCHANGE = "filmExchange";
    private final DirectorRepo directorRepo;

    private  final MoviesRepo moviesRepo;
    private final EntityManager entityManager;
    private final RabbitTemplate rabbitTemplateUpdate;
    private final Logger logger = LoggerFactory.getLogger(org.example.servicefilm.RabbitMqService.FilmEventListener.class);
    private final SenderService senderService;

    @Autowired
    public FilmEventListenerUpdate(
            DirectorRepo directorRepo,
            MoviesRepo moviesRepo,
            EntityManager entityManager,
            @Qualifier("rabbitTemplateUpdateFilm") RabbitTemplate rabbitTemplateUpdate,
            SenderService senderService
    ) {
        this.directorRepo = directorRepo;
        this.moviesRepo = moviesRepo;
        this.entityManager = entityManager;
        this.rabbitTemplateUpdate = rabbitTemplateUpdate; // лучше переименовать поле
        this.senderService = senderService;
    }


    @RabbitListener(queues = "filmUpdateQueue", containerFactory = "rabbitListenerContainerFactoryUpdateFilm")
    public void handleUpdateMovieEvent(MovieDtoUpdateRM dtoRM){

        byte result; // переменная успеха
        try {
            System.out.println("началось выполнение");
            Movies existingMovie = moviesRepo.findById(dtoRM.getMovieId()).
                    orElseThrow(()->new RuntimeException("movie not found"));
            // Обновление данных фильма

            existingMovie.setTitle(dtoRM.getTitle());
            existingMovie.setDescription(dtoRM.getDescription());
            existingMovie.setGenre_film(dtoRM.getGenreFilm());

            // Сохранение изменений
            moviesRepo.save(existingMovie);

            System.out.println("сохранение данних удачно ");

            MovieCreatedEvent event = new MovieCreatedEvent(
                    existingMovie.getId(),
                    existingMovie.getTitle(),
                    existingMovie.getDescription(),
                    existingMovie.getGenre_film().name()
            );

            System.out.println("отправляем сообщение дальше");
            rabbitTemplateUpdate.convertAndSend(
                   "filmExchange",
                    "filmupdate.elastic",
                    event);
            result = 1;
            senderService.sendWithDirectorResultUpdateMovie(dtoRM.getEmail(),result);

        } catch (Exception e) {
            result = 0;
            logger.error("Ошибка при отправке события в RabbitMQ", e);
            System.out.println("ошибка!!!!!!!!!!!!!");
            senderService.sendWithDirectorResultUpdateMovie(dtoRM.getEmail(),result);
        }


    }


}
