package com.example.oopkursova.Controllers;

import com.example.oopkursova.Entity.Finance;
import com.example.oopkursova.loger.Loggable;
import com.example.oopkursova.Repository.FinanceRepo;
import com.example.oopkursova.Service.FinanceService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/Finance")
public class FinanceControllers {

    private final FinanceRepo financeRepo;
    private final FinanceService financeService;


    public FinanceControllers(FinanceRepo financeRepo, FinanceService financeService) {
        this.financeRepo = financeRepo;
        this.financeService = financeService;
    }

    @Loggable
    @GetMapping("/FinanceFilm")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String GetFormFinance(Model model){
        model.addAttribute("finance", new Finance());
        return "FinanceFilm";
    }

    @Loggable
    @PostMapping("/FinanceFilm")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String createFinanceFilm(@Valid Finance finance){
        financeRepo.save(finance);
       // logger.info("Finance information added for film: {}", finance);
        return "MenuDirectors";
    }

    @Loggable
    @GetMapping("/edit_finance")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String updateFinance(Model model){
        model.addAttribute("finance", new Finance());
        return "edit_finance";
    }
    @Loggable
    @PostMapping("/edit_finance")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String updateFinance(@RequestParam("id") Long id, @ModelAttribute Finance updatedFinance) {
        financeService.updateFinance(id, updatedFinance);
        return "MenuDirectors";
    }


}
