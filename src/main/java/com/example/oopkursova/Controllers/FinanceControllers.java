package com.example.oopkursova.Controllers;

import com.example.oopkursova.Entity.Finance;
import com.example.oopkursova.Loggable;
import com.example.oopkursova.Repository.FinanceRepo;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class FinanceControllers {

    private final FinanceRepo financeRepo;
    private static final Logger logger = LoggerFactory.getLogger(FinanceControllers.class);


    public FinanceControllers(FinanceRepo financeRepo) {
        this.financeRepo = financeRepo;
    }

    @Loggable
    @GetMapping("/FinanceFilm")
    public String GetFormFinance(Model model){
        model.addAttribute("finance", new Finance());
        return "FinanceFilm";
    }

    @Loggable
    @PostMapping("/FinanceFilm")
    public String createFinanceFilm(@Valid Finance finance){
        financeRepo.save(finance);
        logger.info("Finance information added for film: {}", finance);
        return "MenuDirectors";
    }


}
