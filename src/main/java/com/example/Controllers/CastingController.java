package com.example.Controllers;

import com.example.DTO.CastingApplicationDto;
import com.example.DTO.CastingDto;
import com.example.DTO.TrialShootingDto;
import com.example.Entity.CastingApplications;
import com.example.Entity.Trial_Shootings;
import com.example.Enum.ApplicationStatus;
import com.example.RabbitMQ.CastingTask.CastingEventPublisher;
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
    private final CastingEventPublisher castingEventPublisher;

    @Autowired
    public CastingController(CastingService castingService, CastingEventPublisher castingEventPublisher) {
        this.castingService = castingService;
        this.castingEventPublisher = castingEventPublisher;
    }

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



    /** 1.кастинг для членов сьемочных групп продумать как они будут попадать в съемку
     * первая идея - при создании роли член сьемочный группы он создает свой профиль
     * и дальше подает на наш сервер (создадим сервер в котором будут хранится наши юзеры которые ищут работу)
     * тут на сервер наши юзера после полностью заполненого профиля создают свой файл резюме и потом отправят на наш сервер свое резюме
     * это резюме будет отправлено на наше S3 хранилище где она будет хранится , после отправки своей заявки
     * наш юзер будет помечен в активном поиске работы.
     * Детали : 1.Создать сервер который будет обрабатывать заявки которые будут отправлятся юзерами.
     *          2.После , когда юзер создаст свой профиль у него будет возможность отправить свое резюме в наш сервер,
     *          когда он отправит, сообщение уйдет в кролика, кролик обработает что бы этот файл был отправлен к хранилище файлов
     *          тут оно отработает и будет создан путь внутри нашего приложение для нашего юзера,
     *          юзер может создать несколько резюме и отправить нам.
     *          3.Когда юзер отправит сообщение вызовем моментальный ответ, что бы поток не ждал, через асинхронную операцию
     *          а после когда резюме будет загружено мы отправим юзеру на почту сообщение, что резюме было отправлено
     *
     * 2.Когда нашему режиссеру понадобится определенный член сьемочной группы, он на наш сервер отправит запрос ,
     * что нужно член с такими то данными, и наш сервер начнет обработку этого запроса, и после обработки отправит режиссеру
     * людей с нужными данными и режиссер увидит и скажет кого он хочет прийнять на работу,  а кого нет и отправит сообщение,
     * принят или нет, когда такое произойдет что юзера приняли на работу юзеру на почту приходит сообщение
     * что режиссер зовет вас на работу, если юзер принимает заявку , режиссеру идет ответ на почту
     * и члена съемочной группы будет добавлено в команду запросом таким же как и актера , после когда будет создаватся сьемочный день
     * ему на почту прийдет сообщение с деталями
     *
     * Детали : 1. Гет запрос директора на наш сервер , данные что нужны для работников, добавить возможность
     * внести много ролей
     *         2.После отправки запроса наш сообщение уйдет в кролика который начнет обработку и кинет такие то данные
     *         в еластик где и будут хранится данные наших юзеров.
     *         3. Когда еластик ответит он вернет ответ и даст всех членов с совпадаемыми условиями фильтрации
     *         4. После всех операций будет созданы заявки
     *         5. Режиссеру будет отправлено данные каждой заявки и резюме
     *         6. Режиссер вводит номер заявки и выбирает принять или отказать
     *         7. После пост запроса будет отправлено сообщение кролику который отправит сообщение юзеру на почту
     *         8. Для принятия или отказа запросу ,у юзера свой пут запрос на выбор
     *         9. Если юзер принял запрос вызывается метод для отправки нашего юзера в команду
     *         */

}
