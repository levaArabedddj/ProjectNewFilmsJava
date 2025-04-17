package com.example.ElasticSearch;

import com.example.ElasticSearch.ClassDocuments.ActorDocument;
import com.example.ElasticSearch.ClassDocuments.CrewMemberDocument;
import com.example.ElasticSearch.Service.ActorService;

import com.example.ElasticSearch.Service.CrewMemberServiceElastic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/elastic")
public class ElasticSearchController {


    private final ActorService actorService;
    private final CrewMemberServiceElastic crewMemberService;

    @Autowired
    public ElasticSearchController(ActorService actorService, CrewMemberServiceElastic crewMemberService) {
        this.actorService = actorService;
        this.crewMemberService = crewMemberService;
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

}
