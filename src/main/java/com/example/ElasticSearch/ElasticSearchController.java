package com.example.ElasticSearch;

import com.example.ElasticSearch.ClassDocuments.ActorDocument;
import com.example.ElasticSearch.ClassDocuments.CrewMemberDocument;
import com.example.ElasticSearch.ClassDocuments.MovieDocument;
import com.example.ElasticSearch.Service.ActorService;

import com.example.ElasticSearch.Service.CrewMemberServiceElastic;
import com.example.ElasticSearch.Service.MovieElasticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/elastic")
public class ElasticSearchController {


    private final ActorService actorService;
    private final CrewMemberServiceElastic crewMemberService;

    @Autowired
    private final MovieElasticService elasticService;
    @Autowired
    public ElasticSearchController(ActorService actorService, CrewMemberServiceElastic crewMemberService, MovieElasticService elasticService) {
        this.actorService = actorService;
        this.crewMemberService = crewMemberService;
        this.elasticService = elasticService;
    }


    // Эндпоинт для получения всех актёров
    @GetMapping("/actors")
    public ResponseEntity<List<ActorDocument>> getAllActors() {
        List<ActorDocument> actors = actorService.getAllActors();
        if (actors == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(actors);
    }

    // Эндпоинт для создания нового актора
    @PostMapping("/actors")
    public ResponseEntity<String> createActor(@RequestBody ActorDocument actor) {
        actorService.indexActor(actor);
        return ResponseEntity.status(HttpStatus.CREATED).body("Актёр успешно создан");
    }

    @PostMapping("/crewMember")
    ResponseEntity<String> createCrewMember(@RequestBody CrewMemberDocument crewMemberDocument)  {
        crewMemberService.indexCrewMember(crewMemberDocument);
        return ResponseEntity.status(HttpStatus.CREATED).body("Член сьемочной группы создан");
    }

    @GetMapping("/crewMember")
    ResponseEntity<List<CrewMemberDocument>> getAllCrewMembers() {

        List<CrewMemberDocument> crewMemberDocuments = crewMemberService.getCrewMember();
        return ResponseEntity.ok(crewMemberDocuments);
    }




     //1. Поиск фильмов по точному названию

    @GetMapping("/title")
    public ResponseEntity<List<MovieDocument>> getByTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            List<MovieDocument> movies = elasticService.getMovieByTitle(title, size);
            return ResponseEntity.ok(movies);
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }


//     2.Поиск фильмов по жанру

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<MovieDocument>> getByGenre(@PathVariable String genre) throws IOException {
        List<MovieDocument> list = elasticService.getGenreBy(genre);
        return ResponseEntity.ok(list);
    }


     //     3. Полнотекстовый поиск в описании
    @GetMapping("/search")
    public ResponseEntity<List<MovieDocument>> fullTextSearch(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String text,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) throws IOException {
        if (title != null && genre != null && text != null) {
            List<MovieDocument> list = elasticService.complexSearch(title, genre, text, from, size);
            return ResponseEntity.ok(list);
        }

        return ResponseEntity.ok(List.of());
    }


}

