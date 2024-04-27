package com.example.oopkursova.Controllers;

import com.example.oopkursova.Entity.ShootingDay;
import com.example.oopkursova.Repository.ShootingDayRepo;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class shootingDayController {

    private final ShootingDayRepo shootingDayRepo;
    private static final Logger logger = LoggerFactory.getLogger(shootingDayController.class);


    public shootingDayController(ShootingDayRepo shootingDayRepo) {
        this.shootingDayRepo = shootingDayRepo;
    }

    @PostMapping("/create_shootingDay")
    public String CreateShootingDay(@Valid ShootingDay shootingDay, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            logger.error("Error creating shooting day: {}", bindingResult.getAllErrors());
            return "create_shootingDay";
        }
        shootingDayRepo.save(shootingDay);
        logger.info("Shooting day created: {}", shootingDay);
        return "MenuDirectors";
    }


    @GetMapping("/create_shootingDay")
    public String CreateShootingDayGet(Model model){
       model.addAttribute("dayShooting",new ShootingDay());
        return "create_shootingDay";
    }
}
