package org.example.servicefilm.RabbitMqDeleteMovie;

import jakarta.persistence.EntityManager;
import org.example.servicefilm.DirectorRepo;
import org.example.servicefilm.Entity.Movies;
import org.example.servicefilm.MoviesRepo;
import org.example.servicefilm.SenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

@Component
public class FilmEventListenerDelete {

    public static final String INDEX_EXCHANGE = "filmDeleteExchange";
    private final DirectorRepo directorRepo;

    private  final MoviesRepo moviesRepo;
    private final EntityManager entityManager;
    private final RabbitTemplate rabbitTemplateDelete;
    private final Logger logger = LoggerFactory.getLogger(org.example.servicefilm.RabbitMqService.FilmEventListener.class);
    private final SenderService senderService;

    public FilmEventListenerDelete(DirectorRepo directorRepo, MoviesRepo moviesRepo,
                                   EntityManager entityManager, @Qualifier("rabbitTemplateDeleteFilm")RabbitTemplate rabbitTemplateDelete,
                                   SenderService senderService) {
        this.directorRepo = directorRepo;
        this.moviesRepo = moviesRepo;
        this.entityManager = entityManager;
        this.rabbitTemplateDelete = rabbitTemplateDelete;
        this.senderService = senderService;
    }


    @RabbitListener(queues = "filmDeleteQueueService", containerFactory = "rabbitListenerContainerFactoryDeleteFilm")
    public void handleDeleteMovieEvent(DeleteDto dto) {

        byte result; // переменная успеха
        try {
            logger.info("Попытка удалить фильм {} пользователем {}", dto.getFilm_id(), dto.getUsername());
            Movies movie = moviesRepo
                    .findByIdAndDirectorUserUserName(dto.getFilm_id(), dto.getUsername())
                    .orElseThrow(() -> new ResourceAccessException(
                            "Film not found or you have no permission to delete it"));


            moviesRepo.delete(movie);



            rabbitTemplateDelete.convertAndSend(
                    INDEX_EXCHANGE,
                    "filmdelete.elastic",
                    movie.getId());

            result = 1;
            senderService.sendWithDirectorResultDeleteMovie(dto.getGmail(),result);

        } catch (Exception e) {
            result = 0;
            logger.error("Ошибка при отправке события в RabbitMQ", e);
            System.out.println("ошибка!!!!!!!!!!!!!");
            senderService.sendWithDirectorResultDeleteMovie(dto.getGmail(),result);
        }


    }
}
