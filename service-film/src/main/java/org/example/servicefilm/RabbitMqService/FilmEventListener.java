package org.example.servicefilm.RabbitMqService;


import jakarta.persistence.EntityManager;
import org.example.servicefilm.*;
import org.example.servicefilm.Entity.Director;
import org.example.servicefilm.Entity.Movies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FilmEventListener {

    public static final String INDEX_EXCHANGE = "elasticsearch_exchange";
    private final DirectorRepo directorRepo;

    private  final MoviesRepo moviesRepo;
    private final EntityManager entityManager;
    private final RabbitTemplate rabbitTemplate;
    private final Logger logger = LoggerFactory.getLogger(FilmEventListener.class);
    private final SenderService senderService;
    @Autowired
    public FilmEventListener(DirectorRepo directorRepo, MoviesRepo moviesRepo, EntityManager entityManager, RabbitTemplate rabbitTemplate, SenderService senderService) {
        this.directorRepo = directorRepo;
        this.moviesRepo = moviesRepo;
        this.entityManager = entityManager;
        this.rabbitTemplate = rabbitTemplate;
        this.senderService = senderService;
    }

    @RabbitListener(queues = "filmCreateQueue")
    public void handleCreateMovieEvent(MovieDtoRM dtoRM){

        byte result; // переменная успеха
        try {
            System.out.println("началось выполнение");
            Long directorId = directorRepo.findIdBuUserId(dtoRM.getUserId())
                    .orElseThrow(() -> new Exception("You are not a director"));
            Director directorRef = entityManager.getReference(Director.class, directorId);

            // 4) Создаём новую сущность фильма
            Movies newMovie = new Movies();
            newMovie.setTitle(dtoRM.getTitle());
            newMovie.setDescription(dtoRM.getDescription());
            newMovie.setGenre_film(dtoRM.getGenreFilm());
            newMovie.setDevelopmentStage(DevelopmentStage.CONCEPT);
            newMovie.setDirector(directorRef);

            // 5) Сохраняем в БД
            moviesRepo.save(newMovie);
            System.out.println("началось сохранение");

            // 6) Индексируем в Elasticsearch, но сначала в RabbitMQ
            MovieCreatedEvent evt = new MovieCreatedEvent(
                    newMovie.getId(),
                    newMovie.getTitle(),
                    newMovie.getDescription(),
                    newMovie.getGenre_film().name()
            );
            System.out.println("начинаем отправку обратно в кролика");
            rabbitTemplate.convertAndSend(
                    INDEX_EXCHANGE,
                    "movies.created",
                    evt
            );
            System.out.println("началось отправка дальше");
            logger.info(" опубликован  MovieCreatedEvent в RabbitMQ: {}", evt);
            result = 1;
            senderService.sendWithDirectorResultCreateMovie(dtoRM.getEmail(),result);
        } catch (Exception e) {
            result = 0;
            logger.error("Ошибка при отправке события в RabbitMQ", e);
            System.out.println("ошибка!!!!!!!!!!!!!");
            senderService.sendWithDirectorResultCreateMovie(dtoRM.getEmail(),result);
        }


    }
}
