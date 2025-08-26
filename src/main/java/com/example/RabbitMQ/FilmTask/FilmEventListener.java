//package com.example.RabbitMQ.FilmTask;
//
//import com.example.DTO.MovieCreatedEvent;
//import com.example.Entity.Director;
//import com.example.Entity.Movies;
//import com.example.Enum.DevelopmentStage;
//import com.example.Exception.ApiException;
//import com.example.RabbitMQ.DtoRabbitMQ.MovieDtoRM;
//import com.example.RabbitMQ.ElasticTask.ElasticConfigQueue;
//import com.example.Repository.DirectorRepo;
//import com.example.Repository.MoviesRepo;
//import com.example.Service.SenderService;
//import jakarta.persistence.EntityManager;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class FilmEventListener {
//
//
//    private final DirectorRepo directorRepo;
//
//    private  final MoviesRepo moviesRepo;
//    private final EntityManager entityManager;
//    private final RabbitTemplate rabbitTemplate;
//    private final Logger logger = LoggerFactory.getLogger(FilmEventListener.class);
//    private final SenderService senderService;
//    @Autowired
//    public FilmEventListener(DirectorRepo directorRepo, MoviesRepo moviesRepo, EntityManager entityManager, RabbitTemplate rabbitTemplate, SenderService senderService) {
//        this.directorRepo = directorRepo;
//        this.moviesRepo = moviesRepo;
//        this.entityManager = entityManager;
//        this.rabbitTemplate = rabbitTemplate;
//        this.senderService = senderService;
//    }
//
//    @RabbitListener(queues = "filmCreateQueue")
//    public void handleCreateMovieEvent(MovieDtoRM dtoRM){
//        logger.info("Received MovieDtoRM: userId={}, title={}", dtoRM.getUserId(), dtoRM.getTitle());
//        byte result; // переменная успеха
//        try {
//            Long directorId = directorRepo.findIdBuUserId(dtoRM.getUserId())
//                    .orElseThrow(() -> new ApiException("You are not a director"));
//            Director directorRef = entityManager.getReference(Director.class, directorId);
//
//            // 4) Создаём новую сущность фильма
//            Movies newMovie = new Movies();
//            newMovie.setTitle(dtoRM.getTitle());
//            newMovie.setDescription(dtoRM.getDescription());
//            newMovie.setGenre_film(dtoRM.getGenreFilm());
//            newMovie.setDevelopmentStage(DevelopmentStage.CONCEPT);
//            newMovie.setDirector(directorRef);
//
//            // 5) Сохраняем в БД
//            moviesRepo.save(newMovie);
//
//            // 6) Индексируем в Elasticsearch, но сначала в RabbitMQ
//            MovieCreatedEvent evt = new MovieCreatedEvent(
//                    newMovie.getId(),
//                    newMovie.getTitle(),
//                    newMovie.getDescription(),
//                    newMovie.getGenre_film().name()
//            );
//            rabbitTemplate.convertAndSend(
//                    ElasticConfigQueue.INDEX_EXCHANGE,
//                    "movies.created",
//                    evt
//            );
//            logger.info(" опубликован  MovieCreatedEvent в RabbitMQ: {}", evt);
//            result = 1;
//            senderService.sendWithDirectorResultCreateMovie(dtoRM.getEmail(),result);
//        } catch (Exception e) {
//            result = 0;
//            senderService.sendWithDirectorResultCreateMovie(dtoRM.getEmail(),result);
//        }
//
//
//    }
//}
