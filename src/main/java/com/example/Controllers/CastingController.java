package com.example.Controllers;

import com.example.DTO.CastingApplicationDto;
import com.example.DTO.CastingDto;
import com.example.DTO.TrialShootingDto;
import com.example.Entity.CastingApplications;
import com.example.Entity.Castings;
import com.example.Entity.Trial_Shootings;
import com.example.Enum.ApplicationStatus;
import com.example.Service.CastingService;
import com.example.config.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/casting")
public class CastingController {

    private final CastingService castingService;



    @Autowired
    public CastingController(CastingService castingService) {
        this.castingService = castingService;
    }


    // тут проработать кто какие ендпоинты может использовать
    // и какие не может , желательно для всех контроллеров осмотреть по логике




    /**
     * Создание кастинга для фильма
     */

    @PostMapping("/create/{movieId}")
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public ResponseEntity<?> createCasting(
            @PathVariable Long movieId,
            @RequestBody CastingDto castingsBody) {

        // Получаем ID текущего пользователя из токена
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((MyUserDetails) authentication.getPrincipal()).getUser_id();

        if (castingsBody.getRoleName() == null || castingsBody.getRoleName().isEmpty()) {
            return ResponseEntity.badRequest().body("Role names cannot be null or empty");
        }

        castingService.createCastings(userId, movieId, castingsBody);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("created");
    }

    /**
     * Актер подает заявку на кастинг
     */
    @PostMapping("/apply/{castingId}")
    @PreAuthorize("hasAuthority('ROLE_ACTOR')")
    public ResponseEntity<?> applyForCasting(
            @PathVariable int castingId,
            @RequestBody String message) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((MyUserDetails) authentication.getPrincipal()).getUser_id();
        CastingApplications application = castingService.applyForCasting(userId, castingId, message);
        return ResponseEntity.ok("Заявка подана");
    }



    /**
     * получения всех кастингов директором
    */
    @GetMapping("/allCastingsMovie/{filmId}")
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public ResponseEntity<?> getAllCastingsMovie(@PathVariable long filmId, Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((MyUserDetails) authentication.getPrincipal()).getUser_id();
        List<CastingDto> castingMovie = castingService.getAllCastingForMovieId(userId,filmId,principal);
        return ResponseEntity.ok(castingMovie);
    }



    /**
     * Получения всех заявок поданные актерами для съемки в фильме
    */
    @GetMapping("/applications/{filmId}/{castingId}")
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public ResponseEntity<List<CastingApplicationDto>> getAllCastingApplications(
            @PathVariable long filmId,
            @PathVariable int castingId,
            Principal principal) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        long userId = ((MyUserDetails) authentication.getPrincipal()).getUser_id();
        List<CastingApplicationDto> applications = castingService.getAllCastingApplications(castingId,filmId, userId, principal);
        return ResponseEntity.ok(applications);
    }


    /**
     * Режиссер рассматривает заявку актера
     */
    @PutMapping("/review/{applicationId}")
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public ResponseEntity<?> reviewApplication(
            @PathVariable Long applicationId,
            @RequestBody Map<String, Object> requestBody) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Current user: " + authentication.getName());
        System.out.println("Roles: " + authentication.getAuthorities());

        ApplicationStatus status = ApplicationStatus.valueOf(requestBody.get("status").toString());
        String feedback = requestBody.get("feedback").toString();
        CastingApplications updatedApplication = castingService.reviewApplication(applicationId, status,feedback);
        return ResponseEntity.ok("заявка принята");
    }
    /**
     Режиссер создает день пробных сьемок
     **/

    @PostMapping("/trialShooting/create/{movieId}")
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public ResponseEntity<?> createTrialShooting(
            @PathVariable Long movieId,
            @RequestBody TrialShootingDto trialDto
            ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((MyUserDetails) authentication.getPrincipal()).getUser_id();

        Trial_Shootings createdTrialShooting = castingService.createTrialShooting(userId,movieId,trialDto);
        return ResponseEntity.ok("Пробный день съемки  создано!");

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
            @RequestBody String roleRequest) {

         // Обработка регистра
        castingService.approveActorForMovie(trialParticipantId, movieId, roleRequest);
        return ResponseEntity.ok("Actor successfully added to the film team");
    }


}
