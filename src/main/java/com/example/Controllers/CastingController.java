package com.example.Controllers;

import com.example.DTO.CastingApplicationDto;
import com.example.DTO.ReviewRequest;
import com.example.Entity.CastingApplications;
import com.example.Entity.Castings;
import com.example.Entity.RoleRequest;
import com.example.Enum.ApplicationStatus;
import com.example.Enum.FilmRole;
import com.example.Service.CastingService;
import com.example.config.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/casting")
public class CastingController {

    private final CastingService castingService;


    // тут проработать кто какие ендпоинты может использовать
    // и какие не может , желательно для всех контроллеров осмотреть по логике


    @Autowired
    public CastingController(CastingService castingService) {
        this.castingService = castingService;
    }
    /**
     * Создание кастинга для фильма
     */

    @PostMapping("/create/{movieId}")
    public ResponseEntity<?> createCasting(
            @PathVariable Long movieId,
            @RequestBody Castings castingsBody) {

        // Получаем ID текущего пользователя из токена
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((MyUserDetails) authentication.getPrincipal()).getUser_id();

        if (castingsBody.getRoleName() == null || castingsBody.getRoleName().isEmpty()) {
            return ResponseEntity.badRequest().body("Role names cannot be null or empty");
        }

        Castings castings = castingService.createCastings(userId, movieId, castingsBody );
        return ResponseEntity.ok("castings created");
    }

    /**
     * Актер подает заявку на кастинг
     */
    @PostMapping("/apply/{actorId}/{castingId}")
    public ResponseEntity<?> applyForCasting(
            @PathVariable Long actorId,
            @PathVariable int castingId,
            @RequestBody String message) {


        CastingApplications application = castingService.applyForCasting(actorId, castingId, message);
        return ResponseEntity.ok("Заявка подана");
    }


    @GetMapping("/applications/{filmId}/{castingId}/{directorId}")
    public ResponseEntity<List<CastingApplicationDto>> getAllCastingApplications(
            @PathVariable int castingId,
            @PathVariable long filmId,
            @PathVariable long directorId,
            Principal principal) {
        List<CastingApplicationDto> applications = castingService.getAllCastingApplications(castingId, filmId, directorId, principal);
        return ResponseEntity.ok(applications);
    }


    /**
     * Режиссер рассматривает заявку актера
     */
    @PutMapping("/review/{applicationId}")
    public ResponseEntity<?> reviewApplication(
            @PathVariable Long applicationId,
            @RequestBody ReviewRequest request) { // єтот момент исправить

        CastingApplications updatedApplication = castingService.reviewApplication(applicationId, request.getStatus(),request.getFeedback());
        return ResponseEntity.ok("заявка принята");
    }

    /**
     * Назначить актера на пробные съемки
     */
    @PostMapping("/assign-trial/{applicationId}/{trialId}")
    public ResponseEntity<String> assignToTrial(
            @PathVariable Long applicationId,
            @PathVariable Long trialId) {

        castingService.assignToTrial(applicationId, trialId);
        return ResponseEntity.ok("Actor successfully assigned to trial shooting");
    }

    /**
     * Одобрить актера после пробных съемок и добавить в команду фильма
     */
    @PostMapping("/approve-actor/{trialParticipantId}/{movieId}")
    public ResponseEntity<String> approveActorForMovie(
            @PathVariable Long trialParticipantId,
            @PathVariable Long movieId,
            @RequestBody RoleRequest roleRequest) { // єтот момент исправить

        FilmRole filmRole = FilmRole.valueOf(roleRequest.getRole().toUpperCase());  // Обработка регистра
        castingService.approveActorForMovie(trialParticipantId, movieId, filmRole);
        return ResponseEntity.ok("Actor successfully added to the film team");
    }


}
